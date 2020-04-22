package com.redroundrobin.thirema.gateway.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Device {
  private final int deviceId;
  private String gateway;
  private final List<Sensor> sensors;

  private int frequency;

  private transient long lastSent;

  public Device(int deviceId) {
    this.deviceId = deviceId;
    this.lastSent = 0;
    this.sensors = new ArrayList<>();
  }

  public Device(int deviceId, List<Sensor> sensors) {
    this.deviceId = deviceId;
    this.lastSent = 0;
    this.sensors = sensors;
  }

  public Device(int deviceId, String gateway) {
    this.deviceId = deviceId;
    this.lastSent = 0;
    this.sensors = new ArrayList<>();
    this.gateway = gateway;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public long getLastSent() {
    return lastSent;
  }

  public void setLastSent(long lastSent) {
    this.lastSent = lastSent;
  }

  public List<Sensor> getSensors() {
    return sensors;
  }

  public void addSensor(Sensor sensor) {
    sensors.add(sensor);
  }

  public void removeSensor(Sensor s) {
    sensors.remove(s);
  }

  public int getSensorsNumber() {
    return sensors.size();
  }

  public int getFrequency() {
    return frequency;
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public String getGateway() {
    return gateway;
  }
}
