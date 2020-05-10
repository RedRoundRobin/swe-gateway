package com.redroundrobin.thirema.gateway.models;

import com.google.gson.Gson;
import java.net.InetAddress;
import java.util.List;

public class Gateway {
  private final InetAddress address;
  private final int port;

  private final String name;
  private final List<Device> devices; // Da prendere dalla configurazione del gateway

  public Gateway(InetAddress address, int port, String name, List<Device> devices) {
    this.address = address;
    this.port = port;

    this.name = name;
    this.devices = devices;
  }

  //crea un gateway partendo da una stringa di configurazione valida
  public static Gateway buildFromConfig(String config) {
    Gson gson = new Gson();
    return gson.fromJson(config, Gateway.class);
  }

  public InetAddress getAddress() {
    return address;
  }

  public List<Device> getDevices() {
    return devices;
  }

  public String getName() {
    return name;
  }

  public int getPort() {
    return port;
  }

  public void addDevice(Device d) {
    devices.add(d);
  }

  public void removeDevice(Device d) {
    devices.remove(d);
  }

  public void addSensorToDevice(Sensor s, Device d) {
    devices.get(devices.indexOf(d)).addSensor(s);
  }

  public void removeSensorFromDevice(Sensor s, Device d) {
    devices.get(devices.indexOf(d)).removeSensor(s);
  }
}

