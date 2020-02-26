package com.redroundrobin.thirema.gateway;

import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Sensor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SgGateway2 {
    public static void main(String[] args) throws UnknownHostException {

        List<Sensor> sensor3 = new ArrayList<>(Arrays.asList(
                new Sensor(1, 0))
        );
        Device device3 = new Device(3, sensor3);

        List<Sensor> sensor4 = new ArrayList<>(Arrays.asList(
                new Sensor(1, 0),
                new Sensor(2, 0))
        );
        Device device4 = new Device(4, sensor4);

        List<Device> devices = new ArrayList<>(Arrays.asList(device3, device4));

        Gateway gateway = new Gateway(InetAddress.getLocalHost(), 6970, "SG-GATEWAY-2", devices,5, 6000);


        gateway.start();
    }
}
