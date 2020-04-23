package com.redroundrobin.thirema.gateway.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DeviceRequest {
  private final InetAddress address;
  private final int port;

  public DeviceRequest(InetAddress address, int port) {
    this.address = address;
    this.port = port;
  }

  public byte[] sendPacket(byte[] requestBuffer) throws IOException {
    byte[] responseBuffer = new byte[requestBuffer.length];

    try (DatagramSocket socket = new DatagramSocket()) {
      DatagramPacket requestDatagram = new DatagramPacket(requestBuffer, requestBuffer.length,
          address, port);
      socket.send(requestDatagram);

      DatagramPacket responseDatagram = new DatagramPacket(responseBuffer, responseBuffer.length);
      socket.setSoTimeout(1000);
      socket.receive(responseDatagram);
    }

    return responseBuffer;
  }

}
