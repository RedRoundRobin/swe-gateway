package com.redroundrobin.thirema.gateway.models;

import java.util.ArrayList;
import java.util.List;

public class Device {
  private final int deviceId;
  private long timestamp;
  private String gateway;
  private final List<Sensor> sensors;

  public Device(int deviceId) {
    this.deviceId = deviceId;
    this.timestamp = 0;
    this.sensors = new ArrayList<>();
  }

  public Device(int deviceId, List<Sensor> sensors) {
    this.deviceId = deviceId;
    this.timestamp = 0;
    this.sensors = sensors;
  }

  public Device(int deviceId, String gateway) {
    this.deviceId = deviceId;
    this.timestamp = 0;
    this.sensors = new ArrayList<>();
    this.gateway = gateway;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public List<Sensor> getSensors() {
    return sensors;
  }

  public void addSensor(Sensor sensor) {
    sensors.add(sensor);
  }

  public void removeSensor(int index) {
    sensors.remove(index);
  }

  public int getSensorsNumber() {
    return sensors.size();
  }
}
