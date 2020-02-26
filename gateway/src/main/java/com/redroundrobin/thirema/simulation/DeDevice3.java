package com.redroundrobin.thirema.simulation;

import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Sensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeDevice3 {

    public static void main(String[] args) {

        List<Sensor> sensors1 = new ArrayList<>(Arrays.asList(
                new Sensor(1, 30),
                new Sensor(2, 30),
                new Sensor(3, 30)));
        Device device1 = new Device(5, sensors1);

        List<Sensor> sensors2 = new ArrayList<>(Arrays.asList(
                new Sensor(1, 35),
                new Sensor(2, 35),
                new Sensor(3, 35),
                new Sensor(4, 120)));
        Device device2 = new Device(6, sensors2);

        List<Device> devices = new ArrayList<>(Arrays.asList(device1, device2));
        DeviceSimulator deviceSimulator = new DeviceSimulator(6971, devices);

        deviceSimulator.start();
    }
}
