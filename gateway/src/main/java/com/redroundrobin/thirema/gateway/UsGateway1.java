package com.redroundrobin.thirema.gateway;

import com.google.gson.Gson;
import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Sensor;
import com.redroundrobin.thirema.gateway.utils.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.redroundrobin.thirema.gateway.Gateway.BuildFromConfig;

public class UsGateway1 {
    public static Gateway createGateway() throws UnknownHostException{
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
        return gateway;
    }

    public static void main(String[] args) throws UnknownHostException {

        Gson gson = new Gson();

        Gateway g = BuildFromConfig(gson.toJson(createGateway()));

        Thread producer = new Thread(new Runnable() {
            @Override
            public void run() {
                g.start();
            }
        });
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                Consumer consumerConfig = new Consumer("US-GATEWAY-1","ConsumerUS","localhost:29092");
                String newConfig = consumerConfig.executeConsumer();

            }
        });
        producer.start();
        consumer.start();
        // TODO: 03/03/20 trovare una soluzione per interrompere 'producer' quando arriva 'newConfig' /
        //  su consumer
    }
}
