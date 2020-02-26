package com.redroundrobin.thirema.gateway.models;

import java.util.ArrayList;
import java.util.List;

public class Device {
    private int id;
    private long timestamp;
    private List<Sensor> sensors;

    public Device(int id) {
        this.id = id;
        this.timestamp = 0;
        this.sensors = new ArrayList<>();
    }

    public Device(int id, List<Sensor> sensors) {
        this.id = id;
        this.timestamp = 0;
        this.sensors = sensors;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    public int getSensorsNumber() {
        return sensors.size();
    }

    public Sensor getSensor(int sensorId) {
        return sensors.get(sensorId);
    }
}
