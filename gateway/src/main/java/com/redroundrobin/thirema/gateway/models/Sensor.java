package com.redroundrobin.thirema.gateway.models;

public class Sensor {
    private int id;
    private long timestamp;
    private int data;

    public Sensor(int id, int data) {
        this.id = id;
        this.timestamp = 0;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getData() {
        return data;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(int data) {
        this.data = data;
    }
}
