package com.redroundrobin.thirema.simulation;

import com.google.gson.Gson;
import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Sensor;
import com.redroundrobin.thirema.gateway.utils.CustomLogger;
import com.redroundrobin.thirema.gateway.utils.Utility;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import org.apache.commons.lang3.ArrayUtils;

public class DeviceSimulator {
  private final int port;
  private final List<Device> devices;

  private static final Logger logger = CustomLogger.getLogger(DeviceSimulator.class.getName(),
      Level.FINE);

  public DeviceSimulator(int port, List<Device> devices) {
    this.port = port;
    this.devices = devices;
  }

  // Pacchetto di risposta
  private List<Byte> createResponsePacket(int idDevice, int idSensor, Integer data) {
    List<Byte> packet = new ArrayList<>();

    Pair<Optional<Device>,Optional<Sensor>> optionals = deviceAndSensorArePresent(idDevice,
        idSensor);
    Optional<Device> optionalDevice = optionals.getKey();
    Optional<Sensor> optionalSensor = optionals.getValue();

    packet.add((byte) idDevice);
    if (optionalDevice.isPresent() && optionalSensor.isPresent()
        && (data == null || optionalSensor.get().isCmdEnabled())) {
      if (data != null) { // command sent
        optionalSensor.get().setData(data);
        logger.log(Level.FINER, new Gson().toJson(optionalSensor.get()));
      }

      packet.add((byte) 1); // data reply
      packet.add((byte) idSensor);
      packet.add((byte) optionalSensor.get().getData());
      packet.add(Utility.calculateCrc(packet));
    } else {
      packet.add((byte) -1); // risposta con errore
      packet.add((byte) idSensor);
      packet.add(Utility.calculateCrc(packet));
    }

    return packet;
  }

  private Pair<Optional<Device>, Optional<Sensor>> deviceAndSensorArePresent(int idDevice,
                                                                             int idSensor) {
    Optional<Device> optionalDevice = devices.stream()
        .filter(device -> idDevice == device.getDeviceId())
        .findFirst();
    Optional<Sensor> optionalSensor = Optional.empty();

    if (optionalDevice.isPresent()) {
      Device device = optionalDevice.get();

      optionalSensor = device.getSensors().stream()
          .filter(sensor -> idSensor == sensor.getSensorId())
          .findFirst();
    }

    return new Pair<>(optionalDevice, optionalSensor);
  }

  public void start() {
    try (DatagramSocket socket = new DatagramSocket(port)) {
      while (true) {
        byte[] requestBuffer = new byte[5];

        DatagramPacket requestDatagram = new DatagramPacket(requestBuffer, requestBuffer.length);
        socket.setSoTimeout(0); // attesa infinita
        socket.receive(requestDatagram);

        StringBuilder log = new StringBuilder();
        log.append("Received: ").append("[").append(" ");
        for (byte b : requestBuffer) {
          log.append(b).append(" ");
        }
        log.append("]");
        logger.log(Level.FINE, log.toString());

        List<Byte> receivedPacket = Arrays.asList(ArrayUtils.toObject(requestBuffer));
        if (!Utility.checkIntegrity(receivedPacket)) {
          logger.log(Level.SEVERE, "Error: corrupted packet!");
          continue;
        }

        List<Byte> responsePacket;
        if (receivedPacket.get(1) == 0) {
          responsePacket = createResponsePacket(Byte.toUnsignedInt(receivedPacket.get(0)),
              Byte.toUnsignedInt(receivedPacket.get(2)), null);
        } else {
          responsePacket = createResponsePacket(Byte.toUnsignedInt(receivedPacket.get(0)),
              Byte.toUnsignedInt(receivedPacket.get(2)), Byte.toUnsignedInt(receivedPacket.get(3)));
        }

        log = new StringBuilder();
        log.append("Sent: ").append("[").append(" ");
        for (byte b : responsePacket) {
          log.append(b).append(" ");
        }
        log.append("]");
        logger.log(Level.FINE, log.toString());

        byte[] responseBuffer = Utility.convertPacket(
            responsePacket.stream().mapToInt(Byte::byteValue).toArray());

        DatagramPacket responseDatagram = new DatagramPacket(responseBuffer, responseBuffer.length,
            requestDatagram.getAddress(), requestDatagram.getPort());
        socket.send(responseDatagram);

        logger.log(Level.INFO, "Success: packet sent!");
      }

    } catch (SocketTimeoutException exception) {
      logger.log(Level.INFO, () -> "Timeout error: " + exception.getMessage());
    } catch (IOException exception) {
      logger.log(Level.INFO, () -> "Client error: " + exception.getMessage());
    }
  }
}
