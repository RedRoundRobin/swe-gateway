package com.redroundrobin.thirema.gateway.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

public class DeviceTest {

    private Device device1;
    private Device device2;
    private Sensor sensor1;
    private Sensor sensor2;

    @Before
    public void setUp() {
        sensor1 = new Sensor(1, 1);
        sensor2 = new Sensor(2, 51);
        device1 = new Device(1);
        List<Sensor> sensorListDevice2 = new ArrayList<>();
        sensorListDevice2.add(sensor1);
        sensorListDevice2.add(sensor2);
        device2 = new Device(2, sensorListDevice2);
    }

    @Test
    public void getDeviceIdTest() {
        assertEquals(1, device1.getDeviceId());
    }

    @Test
    public void getTimestampTest() {
        assertEquals(0, device1.getTimestamp());
    }

    @Test
    public void setTimestampTest() {
        device1.setTimestamp(12);
        assertEquals(12, device1.getTimestamp());
    }

    @Test
    public void addSensorTest() {
        device1.addSensor(new Sensor(1, 1));
        assertEquals(1, device1.getSensorsNumber());
    }

    @Test
    public void removeSensorTest() {
        device1.addSensor(new Sensor(1, 1));
        assertEquals(1, device1.getSensorsNumber());
        device1.removeSensor(0);
        assertEquals(0, device1.getSensorsNumber());
    }

    @Test
    public void getSensorsNumberDevice1Test() {
        device1.addSensor(new Sensor(1, 1));
        assertEquals(1, device1.getSensorsNumber());
    }

    @Test
    public void getSensorsNumberDevice2Test() {
        assertEquals(2, device2.getSensorsNumber());
    }

    @Test
    public void getSensorsDevice2Test() {
        List<Sensor> s = device2.getSensors();
        assertEquals(sensor1, s.get(0));
        assertEquals(sensor2, s.get(1));
    }
}
