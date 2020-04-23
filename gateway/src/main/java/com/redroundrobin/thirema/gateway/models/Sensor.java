package com.redroundrobin.thirema.gateway.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.security.SecureRandom;

public class Sensor {
  private final int sensorId;
  private long timestamp;
  private int data;
  private final boolean cmdEnabled;

  public Sensor(int sensorId, int data) {
    this.sensorId = sensorId;
    this.timestamp = 0;
    this.data = data;
    this.cmdEnabled = false;
  }

  public Sensor(int sensorId, int data, boolean cmdEnabled) {
    this.sensorId = sensorId;
    this.timestamp = 0;
    this.data = data;
    this.cmdEnabled = cmdEnabled;
  }

  public int getSensorId() {
    return sensorId;
  }

  @JsonIgnore
  public int getData() {
    if (!cmdEnabled) {
      SecureRandom rand = new SecureRandom();
      return rand.nextBoolean() ? data + rand.nextInt(2) : data - rand.nextInt(2);
    } else {
      return data;
    }
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
