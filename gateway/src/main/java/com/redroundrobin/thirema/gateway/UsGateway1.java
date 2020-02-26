package com.redroundrobin.thirema.gateway;

import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Sensor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsGateway1 {

    public static void main(String[] args) throws UnknownHostException {

        List<Sensor> sensor1 = new ArrayList<>(Arrays.asList(
                new Sensor(1, 0),
                new Sensor(2, 0))
        );
        Device device1 = new Device(1, sensor1);

        List<Sensor> sensor2 = new ArrayList<>(Arrays.asList(
                new Sensor(1, 0))
        );
        Device device2 = new Device(2, sensor2);

        List<Device> devices = new ArrayList<>(Arrays.asList(device1, device2));

        Gateway gateway = new Gateway(InetAddress.getLocalHost(), 6969, "US-GATEWAY-1", devices,5, 6000);

        gateway.start();
    }
}
