package com.redroundrobin.thirema.arduinoDevice;


import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.google.gson.Gson;
import com.redroundrobin.thirema.gateway.utils.CustomLogger;
import com.redroundrobin.thirema.gateway.utils.Utility;
import com.redroundrobin.thirema.simulation.DeviceSimulator;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TwoWaySerialComm
{
  private static final Logger logger = CustomLogger.getLogger(DeviceSimulator.class.getName(),
      Level.FINE);

  private SerialPort devicePort;
  private int deviceId;
  private Scanner data;

  public TwoWaySerialComm(int deviceId, SerialPort devicePort) {
    this.deviceId = deviceId;

    this.devicePort = devicePort;
    this.devicePort.setComPortParameters(2000000, 8, 1, SerialPort.NO_PARITY);
    this.devicePort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
    this.data = new Scanner(this.devicePort.getInputStream());
  }

  public void openPort() {
    devicePort.openPort();
  }

  public void closePort() {
    devicePort.closePort();
  }

  public void sendRequest(DatagramSocket socket, DatagramPacket requestDatagram, byte[] b) throws IOException {
    logger.log(Level.FINE, "Req: " + new Gson().toJson(b));

    devicePort.addDataListener(new SerialPortDataListener() {
      @Override
      public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
      }

      @Override
      public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
          logger.log(Level.FINE, "event: " + event.getEventType());
          return;
        }

        try {
          byte[] response = event.getReceivedData();
          logger.log(Level.FINE, "Res: " + new Gson().toJson(response));

          List<Byte> responsePacket = new ArrayList<>();

          responsePacket.add((byte)deviceId);
          if (response.length == 3) {
            responsePacket.add(response[1]);
            responsePacket.add(response[0]);
            responsePacket.add(response[2]);
          } else {
            responsePacket.add((byte) -1);
            responsePacket.add((byte) 0);
            responsePacket.add((byte) 0);
          }
          responsePacket.add(Utility.calculateCrc(responsePacket));

          logger.log(Level.FINE, new Gson().toJson(responsePacket));

          byte[] responseBuffer = Utility.convertPacket(
              responsePacket.stream().mapToInt(Byte::byteValue).toArray());

          DatagramPacket responseDatagram = new DatagramPacket(responseBuffer, responseBuffer.length,
              requestDatagram.getAddress(), requestDatagram.getPort());

          socket.send(responseDatagram);
          logger.log(Level.INFO, "Success: packet sent!");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    devicePort.writeBytes(b, b.length);
    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    devicePort.removeDataListener();
  }

  public static void main(String[] args) throws InterruptedException, IOException {

    TwoWaySerialComm serial = new TwoWaySerialComm(1, SerialPort.getCommPorts()[0]);
    serial.openPort();
    // {sensor, type=[0 = richiesta dati, 2 = invio comando],
    //    data=[da settare in caso di invio comando, 0 altrimenti]}

    try (DatagramSocket socket = new DatagramSocket(6969)) {
      while (true) {
        byte[] requestBuffer = new byte[5];

        DatagramPacket requestDatagram = new DatagramPacket(requestBuffer, requestBuffer.length);
        socket.setSoTimeout(0); // attesa infinita
        socket.receive(requestDatagram);

        logger.log(Level.FINE, new Gson().toJson(requestBuffer));

        List<Byte> receivedPacket = Arrays.asList(ArrayUtils.toObject(requestBuffer));
        if (!Utility.checkIntegrity(receivedPacket)) {
          logger.log(Level.SEVERE, "Error: corrupted packet!");
          continue;
        }

        byte[] bytesToSend = {requestBuffer[2],requestBuffer[1],requestBuffer[3]};

        serial.sendRequest(socket, requestDatagram, bytesToSend);

      }

    } catch (SocketTimeoutException exception) {
      logger.log(Level.INFO, () -> "Timeout error: " + exception.getMessage());
    } catch (IOException exception) {
      logger.log(Level.INFO, () -> "Client error: " + exception.getMessage());
    }
  }
}