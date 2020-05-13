package com.redroundrobin.thirema.gateway;

import static com.redroundrobin.thirema.gateway.utils.Utility.calculateCrc;
import static com.redroundrobin.thirema.gateway.utils.Utility.sendPacket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Gateway;
import com.redroundrobin.thirema.gateway.models.Sensor;
import com.redroundrobin.thirema.gateway.threads.CmdConsumer;
import com.redroundrobin.thirema.gateway.utils.CustomLogger;
import com.redroundrobin.thirema.gateway.utils.Producer;
import com.redroundrobin.thirema.gateway.utils.Translator;
import com.redroundrobin.thirema.gateway.utils.Utility;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;

public class GatewayManager {
  private final Gateway gateway;
  private static final String BOOTSTRAP_SERVER = "kafka-core:29092";
  private static final int SLEEP_TIME = 200;

  private final int maxStoredPacketsPerRequest; // Da prendere dalla configurazione del gateway
  private final int maxStoringTimePerRequest; // Da prendere dalla configurazione del gateway in millisecondi

  private long lastSent;
  private int storedPackets;

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

  public void init() {
    byte operation = (byte) 0;
    byte data = (byte) 0;

    for (Device d : gateway.getDevices()) {
      for (Sensor s : d.getSensors()) {
        try {

          //richiesta ad ogni sensore
          byte deviceId = (byte) d.getDeviceId();
          byte sensorId = (byte) s.getSensorId();

          byte[] requestBuffer = createRequestPacket(deviceId, operation, sensorId, data);
          byte[] responseBuffer = sendPacket(gateway.getAddress(), gateway.getPort(), requestBuffer);

          if (responseBuffer[1] == -1) {
            gateway.removeSensorFromDevice(s, d);
          }

          //Thread.sleep(250); // Da tenere solo per fare test

        } catch (SocketTimeoutException | SocketException timeout) {
          logger.log(Level.SEVERE, () -> "sensore in timeout n" + s.getSensorId()
              + " del device n" + d.getDeviceId());
          gateway.removeSensorFromDevice(s, d);
        } catch (Exception e) {
          logger.log(Level.WARNING, "EXCEPTION!", e);
        }
      }
      if (d.getSensors().isEmpty()) {
        gateway.removeDevice(d);
      }
    }
  }

  // Metodo che reperisce i dati dai dispositivi e dopo averne accumulati "storedPacket" o aver aspettato "storingTime" millisecondi li invia al topic di Kafka specificato
  public void start() {

    Translator translator = new Translator();
    Producer producer = null;

    ExecutorService executorService = Executors.newFixedThreadPool(1);
    CmdConsumer cmdConsumer = new CmdConsumer("cmd-" + getName(), "cmd-" + getName(),
        BOOTSTRAP_SERVER);
    Future<String> command = executorService.submit(cmdConsumer);

    try {
      producer = new Producer(gateway.getName(), BOOTSTRAP_SERVER);

      // Ciclo in cui vengono effettuate tutte le richieste per ogni sensore
      while (true) {
        Thread.sleep(SLEEP_TIME);  // Utilized for let the cpu for others threads
        if (command.isDone()) {
          JsonObject obj = new Gson().fromJson(command.get(), JsonObject.class);

          byte deviceId = (byte) obj.get("realDeviceId").getAsInt(); // prendo uno tra gli id
          byte reqOperation = 2;
          byte sensorId = (byte) obj.get("realSensorId").getAsInt(); // prendo uno dei sensori del dispositivo
          byte reqData = obj.get("data").getAsByte();

          byte[] requestBuffer = createRequestPacket(deviceId, reqOperation, sensorId, reqData);
          sendPacket(gateway.getAddress(), gateway.getPort(), requestBuffer);

          cmdConsumer = new CmdConsumer("cmd-" + getName(),
              "cmd-" + getName(), BOOTSTRAP_SERVER);
          command = executorService.submit(cmdConsumer);
        } else {
          for (Device d : gateway.getDevices()) {
            long timeSinceLastRequest = System.currentTimeMillis() - d.getLastSent();
            if (timeSinceLastRequest > (d.getFrequency() * 1000 - SLEEP_TIME)) {
              sendRequestsByDevice(d, translator, producer);
            }
          }
        }
      }
    } catch (InterruptedException | SocketTimeoutException e) {
      logger.log(Level.WARNING, "Interrupted or Timeout!", e);
      Thread.currentThread().interrupt();
    } catch (Exception exception) {
      logger.log(Level.WARNING, "General exception!", exception);
    } finally {
      if (producer != null) {
        producer.close();
      }
      command.cancel(true);
      executorService.shutdown();
    }
  }

  // Creazione di un pacchetto di richiesta dati per uno dei dispositivi disponibili nel Server
  public byte[] createRequestPacket(byte deviceId, byte operation, byte sensorId, byte data) {
    byte crc = calculateCrc(Arrays.asList(deviceId, operation, sensorId, data));
    return new byte[]{deviceId, operation, sensorId, data, crc};
  }

  private void sendRequestsByDevice(Device d, Translator translator, Producer producer) throws IOException, InterruptedException {
    byte reqOperation = (byte) 0;
    byte reqData = (byte) 0;

    for (Sensor s : d.getSensors()) {
      byte deviceId = (byte) d.getDeviceId();
      byte sensorId = (byte) s.getSensorId();

      byte[] requestBuffer = createRequestPacket(deviceId, reqOperation, sensorId, reqData);
      byte[] responseBuffer = sendPacket(gateway.getAddress(), gateway.getPort(), requestBuffer);

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
}
