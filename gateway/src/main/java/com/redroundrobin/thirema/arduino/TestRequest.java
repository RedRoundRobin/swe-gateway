package com.redroundrobin.thirema.arduino;

import static com.redroundrobin.thirema.gateway.utils.Utility.calculateCrc;
import static com.redroundrobin.thirema.gateway.utils.Utility.sendPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

public class TestRequest {
  public static void main(String[] args) throws IOException {
    byte deviceId = 0;
    byte reqOperation = 0;
    byte sensorId = 4;
    byte reqData = 0;

    byte[] requestBuffer = createRequestPacket(deviceId, reqOperation, sensorId, reqData);
    byte[] responseBuffer = sendPacket(InetAddress.getByName("127.0.1.1"), 6969, requestBuffer);
  }

  public static byte[] createRequestPacket(byte deviceId, byte operation, byte sensorId,
                                           byte data) {
    byte crc = calculateCrc(Arrays.asList(deviceId, operation, sensorId, data));
    return new byte[]{deviceId, operation, sensorId, data, crc};
  }
}
