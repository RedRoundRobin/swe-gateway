package com.redroundrobin.thirema.simulation;

import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Sensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SgDevice2 {

    public static void main(String[] args) {

        List<Sensor> sensors1 = new ArrayList<>(Arrays.asList(
                new Sensor(1, 15),
                new Sensor(2, 120),
                new Sensor(3, 120)));
        Device device1 = new Device(1, sensors1);

        List<Sensor> sensors2 = new ArrayList<>(Arrays.asList(
                new Sensor(1, 20),
                new Sensor(2, 20)));
        Device device2 = new Device(2, sensors2);

        List<Sensor> sensors3 = new ArrayList<>(Arrays.asList(
                new Sensor(1, 120),
                new Sensor(2, 120)));
        Device device3 = new Device(3, sensors3);

        List<Device> devices = new ArrayList<>(Arrays.asList(device1, device2));
        DeviceSimulator deviceSimulator = new DeviceSimulator(6970, devices);

        deviceSimulator.start();
    }
}
