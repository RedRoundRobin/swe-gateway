package com.redroundrobin.thirema.gateway;

import static com.redroundrobin.thirema.gateway.utils.Utility.calculateCrc;

import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Gateway;
import com.redroundrobin.thirema.gateway.models.Sensor;
import com.redroundrobin.thirema.gateway.utils.CustomLogger;
import com.redroundrobin.thirema.gateway.utils.Producer;
import com.redroundrobin.thirema.gateway.utils.Translator;
import com.redroundrobin.thirema.gateway.utils.Utility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;

public class GatewayManager {
  private final Gateway gateway;

  private final int maxStoredPacketsPerRequest; // Da prendere dalla configurazione del gateway
  private final int maxStoringTimePerRequest; // Da prendere dalla configurazione del gateway in millisecondi

  private long lastSent;
  private int storedPackets;
  private DatagramSocket socket;
  private Producer producer;
  private Translator translator;

  private static final Logger logger = CustomLogger.getLogger(GatewayManager.class.getName());

  public GatewayManager(Gateway gateway, int maxStoredPacketsPerRequest,
                        int maxStoringTimePerRequest) {
    this.gateway = gateway;

    this.maxStoredPacketsPerRequest = maxStoredPacketsPerRequest; // Accumulo di pacchetti di default
    this.maxStoringTimePerRequest = maxStoringTimePerRequest; // Tempo di accumulo di default
  }

  public String getName() {
    return gateway.getName();
  }

  public List<Device> getDevices() {
    return gateway.getDevices();
  }

  // Metodo che reperisce i dati dai dispositivi e dopo averne accumulati "storedPacket" o aver aspettato "storingTime" millisecondi li invia al topic di Kafka specificato
  public void start() {

    translator = new Translator();

    try {
      socket = new DatagramSocket();
      producer = new Producer(gateway.getName(), "kafka-core:29092");

      // Ciclo in cui vengono effettuate tutte le richieste per ogni sensore
      while (true) {
        for (Device d : gateway.getDevices()) {
          long timeSinceLastRequest = System.currentTimeMillis() - d.getLastSent();
          if (timeSinceLastRequest > d.getFrequency() * 1000) {
            sendRequestsByDevice(d);
          }
        }
      }
    } catch (InterruptedException | SocketTimeoutException e) {
      logger.log(Level.WARNING, "Interrupted or Timeout!", e);
    } catch (Exception exception) {
      logger.log(Level.WARNING, "General exception!", exception);
    } finally {
      socket.close();
      producer.close();
    }
  }

  private void sendRequestsByDevice(Device d) throws IOException, InterruptedException {
    for (Sensor s : d.getSensors()) {
      byte[] requestBuffer = createRequestPacket(d, s);
      DatagramPacket requestDatagram = new DatagramPacket(requestBuffer, requestBuffer.length,
          gateway.getAddress(), gateway.getPort());
      socket.send(requestDatagram);

      byte[] responseBuffer = new byte[5];
      DatagramPacket responseDatagram = new DatagramPacket(responseBuffer, responseBuffer.length);
      socket.setSoTimeout(15000);
      socket.receive(responseDatagram);

      List<Byte> responsePacket = Arrays.asList(ArrayUtils.toObject(responseBuffer));
      if (Utility.checkIntegrity(responsePacket) && translator.addSensor(responseBuffer,
          gateway.getName())) {
        storedPackets++;
      }
    }

    long timeSpent = System.currentTimeMillis() - lastSent;
    if (storedPackets > maxStoredPacketsPerRequest || timeSpent > maxStoringTimePerRequest) {
      String data = translator.getJson();
      producer.executeProducer(gateway.getName(), data);
      lastSent = System.currentTimeMillis();
      storedPackets = 0;
      translator.clearDevices();
    }

    d.setLastSent(System.currentTimeMillis());
  }

  // Creazione di un pacchetto di richiesta dati per uno dei dispositivi disponibili nel Server
  public byte[] createRequestPacket(Device d, Sensor s) {
    int deviceId = d.getDeviceId();
    int sensorId = s.getSensorId();

    byte device = (byte) (deviceId); // prendo uno tra gli id
    byte operation = 0;
    byte sensor = (byte) (sensorId); // prendo uno dei sensori del dispositivo
    byte data = 0;

    List<Byte> packet = new ArrayList<>(Arrays.asList(device, operation, sensor, data));

    return new byte[]{device, operation, sensor, data, calculateCrc(packet)};
  }

  public void init() {
    for (Device d : gateway.getDevices()) {
      for (Sensor s : d.getSensors()) {
        try {
          socket = new DatagramSocket();

          //richiesta ad ogni sensore
          byte[] requestBuffer = createRequestPacket(d, s);
          DatagramPacket requestDatagram = new DatagramPacket(requestBuffer, requestBuffer.length,
              gateway.getAddress(), gateway.getPort());
          socket.send(requestDatagram);

          //risposta di ognmi sensore
          byte[] responseBuffer = new byte[5];
          DatagramPacket responseDatagram = new DatagramPacket(responseBuffer, responseBuffer.length);
          socket.setSoTimeout(1000);
          socket.receive(responseDatagram);

          if (responseBuffer[1] == -1) {
            d.removeSensor(s);
          }

          //Thread.sleep(250); // Da tenere solo per fare test

        } catch (SocketTimeoutException | SocketException timeout) {
          logger.log(Level.SEVERE, () -> "sensore in timeout n" + s.getSensorId()
              + " del device n" + d.getDeviceId());
          gateway.removeSensorFromDevice(s, d);
        } catch (Exception e) {
          logger.log(Level.WARNING, "EXCEPTION!", e);
        } finally {
          socket.close();
        }
      }
      if (d.getSensors().isEmpty()) {
        gateway.removeDevice(d);
      }
    }
  }
}
