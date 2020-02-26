package com.redroundrobin.thirema.gateway.models;

import java.util.Random;

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
        Random rand = new Random();
        return rand.nextBoolean() ? data + rand.nextInt(2) : data - rand.nextInt(2);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(int data) {
        this.data = data;
    }
}
