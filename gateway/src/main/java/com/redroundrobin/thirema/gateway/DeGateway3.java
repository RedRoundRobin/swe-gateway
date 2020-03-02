package com.redroundrobin.thirema.gateway;

import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Sensor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeGateway3 {
    public static void main(String[] args) throws UnknownHostException {

        List<Sensor> sensor5 = new ArrayList<>(Arrays.asList(
                new Sensor(1, 0),
                new Sensor(2, 0),
                new Sensor(3, 0))
        );
        Device device5 = new Device(5, sensor5);

        List<Sensor> sensor6 = new ArrayList<>(Arrays.asList(
                new Sensor(1, 0),
                new Sensor(2, 0),
                new Sensor(3, 0))
        );
        Device device6 = new Device(6, sensor6);

        List<Device> devices = new ArrayList<>(Arrays.asList(device5, device6));

        Gateway gateway = new Gateway(InetAddress.getLocalHost(), 6971, "DE-GATEWAY-3", devices,5, 6000);

        gateway.start();
    }
}
