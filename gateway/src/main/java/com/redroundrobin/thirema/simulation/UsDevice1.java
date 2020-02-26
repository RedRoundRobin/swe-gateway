package com.redroundrobin.thirema.simulation;

import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Sensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsDevice1 {

    public static void main(String[] args) {
        //Imposto i sensori disponibili per la simulazione
        List<Sensor> sensors1 = new ArrayList<>(Arrays.asList(new Sensor(1, 21), new Sensor(2, 50), new Sensor(3, 4), new Sensor(4, 150)));
        Device device1 = new Device(1, sensors1);

        List<Sensor> sensors2 = new ArrayList<>(Arrays.asList(new Sensor(1, 234), new Sensor(2, 21), new Sensor(3, 32)));
        Device device2 = new Device(2, sensors2);

        List<Sensor> sensors3 = new ArrayList<>(Arrays.asList(new Sensor(1, 21), new Sensor(2, 23), new Sensor(3, 34), new Sensor(4, 54)));
        Device device3 = new Device(3, sensors3);

        List<Sensor> sensors4 = new ArrayList<>(Arrays.asList(new Sensor(1, 13), new Sensor(2, 22), new Sensor(3, 33), new Sensor(4, 44)));
        Device device4 = new Device(4, sensors4);

        List<Sensor> sensors5 = new ArrayList<>(Arrays.asList(new Sensor(1, 17), new Sensor(2, 62), new Sensor(3, 73), new Sensor(4, 47)));
        Device device5 = new Device(5, sensors5);

        List<Sensor> sensors6 = new ArrayList<>(Arrays.asList(new Sensor(1, 61), new Sensor(2, 27), new Sensor(3, 43), new Sensor(4, 46)));
        Device device6 = new Device(6, sensors6);

        List<Device> devices = new ArrayList<>(Arrays.asList(device1, device2, device3, device4, device5, device6));

        DeviceSimulator deviceSimulator = new DeviceSimulator(6969, devices);

        // Avvio del server che aspetta le richieste del gateway
        deviceSimulator.start();
    }
}
