package com.redroundrobin.thirema.gateway.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class DeviceTest {

    @Test
    public void getDeviceId() {
        Device device = new Device(1);
        assertEquals(1, device.getDeviceId());
    }

    @Test
    public void getTimestamp() {
        Device device = new Device(1);
        assertEquals(0, device.getTimestamp());
    }

    @Test
    public void setTimestamp() {
        Device device = new Device(1);
        device.setTimestamp(12);
        assertEquals(12, device.getTimestamp());
    }

    @Test
    public void addSensor() {
        Device device = new Device(1);
        device.addSensor(new Sensor(1, 1));
        assertEquals(1, device.getSensorsNumber());
    }

    @Test
    public void removeSensor() {
        Device device = new Device(1);
        device.addSensor(new Sensor(1, 1));
        assertEquals(1, device.getSensorsNumber());
        device.removeSensor(0);
        assertEquals(0, device.getSensorsNumber());
    }

    @Test
    public void getSensorsNumber() {
        Device device = new Device(1);
        device.addSensor(new Sensor(1, 1));
        assertEquals(1, device.getSensorsNumber());
    }
}