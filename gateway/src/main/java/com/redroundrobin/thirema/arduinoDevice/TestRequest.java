package com.redroundrobin.thirema.arduinoDevice;

import com.redroundrobin.thirema.gateway.utils.DeviceRequest;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

import static com.redroundrobin.thirema.gateway.utils.Utility.calculateCrc;

public class TestRequest {
  public static void main(String[] args) throws IOException {
    byte deviceId = 0;
    byte reqOperation = 0;
    byte sensorId = 4;
    byte reqData = 0;
    DeviceRequest deviceRequest = new DeviceRequest(InetAddress.getByName("127.0.1.1"), 6969);

    byte[] requestBuffer = createRequestPacket(deviceId, reqOperation, sensorId, reqData);
    byte[] responseBuffer = deviceRequest.sendPacket(requestBuffer);
  }

  public static byte[] createRequestPacket(byte deviceId, byte operation, byte sensorId, byte data) {
    byte crc = calculateCrc(Arrays.asList(deviceId, operation, sensorId, data));
    return new byte[]{deviceId, operation, sensorId, data, crc};
  }
}
