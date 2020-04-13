package com.redroundrobin.thirema.simulation;


import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Sensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExampleDeviceSimulator {

  public static void main(String[] args) {

    List<Sensor> sensors1 = new ArrayList<>(Arrays.asList(
        new Sensor(1, 5),
        new Sensor(2, 5),
        new Sensor(3, 120)));
    Device device1 = new Device(1, sensors1);

    List<Sensor> sensors2 = new ArrayList<>(Arrays.asList(
        new Sensor(1, 10),
        new Sensor(2, 120)));
    Device device2 = new Device(2, sensors2);

    List<Sensor> sensors3 = new ArrayList<>(Arrays.asList(
            new Sensor(1, 15),
            new Sensor(2, 120),
            new Sensor(3, 120)));
    Device device3 = new Device(3, sensors3);

    List<Sensor> sensors4 = new ArrayList<>(Arrays.asList(
            new Sensor(1, 20),
            new Sensor(2, 20)));
    Device device4 = new Device(4, sensors4);

    List<Sensor> sensors5 = new ArrayList<>(Arrays.asList(
            new Sensor(1, 30),
            new Sensor(2, 30),
            new Sensor(3, 30)));
    Device device5 = new Device(5, sensors5);

    List<Sensor> sensors6 = new ArrayList<>(Arrays.asList(
            new Sensor(1, 35),
            new Sensor(2, 35),
            new Sensor(3, 35),
            new Sensor(4, 120)));
    Device device6 = new Device(6, sensors6);

    List<Device> devices = new ArrayList<>(Arrays.asList(device1, device2, device3, device4, device5, device6));
    DeviceSimulator deviceSimulator = new DeviceSimulator(6969, devices);

    deviceSimulator.start();
  }
}
