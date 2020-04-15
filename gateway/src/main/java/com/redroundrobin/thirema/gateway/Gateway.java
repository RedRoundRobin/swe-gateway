package com.redroundrobin.thirema.gateway;

import static com.redroundrobin.thirema.gateway.utils.Utility.calculateCrc;

import com.google.gson.Gson;
import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.utils.CustomLogger;
import com.redroundrobin.thirema.gateway.utils.Producer;
import com.redroundrobin.thirema.gateway.utils.Translator;
import com.redroundrobin.thirema.gateway.utils.Utility;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;

public class Gateway {
  private final InetAddress address;
  private final int port;

  private final String name;
  private final List<Device> devices; // Da prendere dalla configurazione del gateway

  private final int storedPacket; // Da prendere dalla configurazione del gateway
  private final int storingTime; // Da prendere dalla configurazione del gateway in millisecondi

  private static final Logger logger = CustomLogger.getLogger(Gateway.class.getName());

  public Gateway(InetAddress address, int port, String name, List<Device> devices, int storedPacket, int storingTime) {
    this.address = address;
    this.port = port;

    this.name = name;
    this.devices = devices;

    this.storedPacket = storedPacket; // Accumulo di pacchetti di default
    this.storingTime = storingTime; // Tempo di accumulo di default
  }

  //crea un gateway partendo da una stringa di configurazione valida
  public static Gateway buildFromConfig(String config) {
    Gson gson = new Gson();
    return gson.fromJson(config, Gateway.class);
  }

  public String getName() {
    return name;
  }

  // Metodo che reperisce i dati dai dispositivi e dopo averne accumulati "storedPacket" o aver aspettato "storingTime" millisecondi li invia al topic di Kafka specificato
  public void start() {
    try (DatagramSocket socket = new DatagramSocket(); Producer producer = new Producer(name, "kafka-core:29092")) {
      Translator translator = new Translator();

      long timestamp = System.currentTimeMillis();
      int packetNumber = 0;
      int deviceNumber = devices.size();
      System.out.println("here");

      // Ciclo in cui vengono effettuate tutte le richieste per ogni sensore
      while (true) {
        for (int disp = 0; disp < deviceNumber; disp++) {
          for (int sens = 0; sens < devices.get(disp).getSensors().size(); sens++) {

            byte[] requestBuffer = createRequestPacket(disp, sens);
            DatagramPacket requestDatagram = new DatagramPacket(requestBuffer, requestBuffer.length, address, port);
            socket.send(requestDatagram);

            byte[] responseBuffer = new byte[5];
            DatagramPacket responseDatagram = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.setSoTimeout(15000);
            socket.receive(responseDatagram);
            List<Byte> responsePacket = Arrays.asList(ArrayUtils.toObject(responseBuffer));
            if (Utility.checkIntegrity(responsePacket) && translator.addSensor(responseBuffer, name)) {
              packetNumber++;

            }
            long timeSpent = System.currentTimeMillis() - timestamp;
            if (packetNumber > storedPacket || timeSpent > storingTime) {
              String data = translator.getJson();
              producer.executeProducer(name, data);
              timestamp = System.currentTimeMillis();
              packetNumber = 0;
            }
            Thread.sleep(250); // Da tenere solo per fare test
          }
        }
      }
    } catch (InterruptedException | SocketTimeoutException e) {
      logger.log(Level.WARNING, "Interrupted or Timeout!", e);
    } catch (Exception exception) {
      logger.log(Level.WARNING, "General exception!", exception);
    }
  }

  // Creazione di un pacchetto di richiesta dati per uno dei dispositivi disponibili nel Server
  public byte[] createRequestPacket(int devIndex, int senIndex) {
    int deviceId = devices.get(devIndex).getDeviceId();
    int sensorId = devices.get(devIndex).getSensors().get(senIndex).getSensorId();

    byte device = (byte) (deviceId); // prendo uno tra gli id
    byte operation = 0;
    byte sensor = (byte) (sensorId); // prendo uno dei sensori del dispositivo
    byte data = 0;

    List<Byte> packet = new ArrayList<>(Arrays.asList(device, operation, sensor, data));

    return new byte[]{device, operation, sensor, data, calculateCrc(packet)};
  }

  public void init() {
    int deviceNumber = devices.size();
    for (int disp = 0; disp < deviceNumber; disp++) {
      for (int sens = 0; sens < devices.get(disp).getSensors().size(); sens++) {
        try (DatagramSocket socket = new DatagramSocket()) {
          //richiesta ad ogni sensore
          byte[] requestBuffer = createRequestPacket(disp, sens);
          DatagramPacket requestDatagram = new DatagramPacket(requestBuffer, requestBuffer.length, address, port);
          socket.send(requestDatagram);

          //risposta di ognmi sensore
          byte[] responseBuffer = new byte[5];
          DatagramPacket responseDatagram = new DatagramPacket(responseBuffer, responseBuffer.length);
          socket.setSoTimeout(150);
          socket.receive(responseDatagram);

          if (responseBuffer[1] == -1) {
            devices.get(disp).removeSensor(sens);
            sens--;
          }

          Thread.sleep(250); // Da tenere solo per fare test

        } catch (SocketTimeoutException | SocketException timeout) {
          final int finalSens = sens;
          final int finalDisp = disp;
          logger.log(Level.SEVERE, () -> "sensore in timeout n" + finalSens + " del device n" + finalDisp);
          devices.get(disp).removeSensor(sens);
          sens--;
        } catch (Exception e) {
          logger.log(Level.WARNING, "EXCEPTION!", e);
        }
      }
      if (devices.get(disp).getSensors().isEmpty()) {
        devices.remove(disp);
        disp--;
        deviceNumber--;
      }
    }
  }
}
