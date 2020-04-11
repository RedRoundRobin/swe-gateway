package com.redroundrobin.thirema.gateway.models;

import java.security.SecureRandom;

public class Sensor {
  private final int sensorId;
  private long timestamp;
  private int data;

  public Sensor(int sensorId, int data) {
    this.sensorId = sensorId;
    this.timestamp = 0;
    this.data = data;
  }

  public int getSensorId() {
    return sensorId;
  }

  public int getData() {
    SecureRandom rand = new SecureRandom();
    return rand.nextBoolean() ? data + rand.nextInt(2) : data - rand.nextInt(2);
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public void setData(int data) {
    this.data = data;
  }
}
