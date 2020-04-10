package com.redroundrobin.thirema.gateway;

import com.google.gson.*;
import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.utils.*;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.redroundrobin.thirema.gateway.Gateway.BuildFromConfig;

public class GatewayClient {

    private static class threadedConsumer implements Callable<String> {
        private Consumer consumerConfig;
        private final String defaultConfig = "{\"address\":\"127.0.1.1\",\"port\":6969,\"name\":\"US-GATEWAY-1\",  \"devices\":  [],  \"storedPacket\":5,  \"storingTime\":6000}";

        public threadedConsumer(String topic, String name, String bootstrapServer){
            this.consumerConfig = new Consumer(topic,name,bootstrapServer);
        }

        @Override
        public String call() {
            String newConfig = consumerConfig.executeConsumer();
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(newConfig).getAsJsonObject();
            int port = -1;

            String address = gson.fromJson(obj.get("address"), String.class);
            port = gson.fromJson(obj.get("port"), int.class);
            String name = gson.fromJson(obj.get("name"), String.class);
            List<Device> devices = new ArrayList<Device>();

            gson.fromJson(obj.get("devices"), JsonArray.class).forEach(dev->{
                Device device = gson.fromJson(dev,Device.class);
                devices.add(device);
            });

            if (address.isEmpty() || port<=-1 || name.isEmpty()){
                return this.defaultConfig;
            }
            return newConfig;
        }
    }

    private static class threadedProducer implements Callable<String> {
        private Gateway gateway;

        public threadedProducer(String config){
            this.gateway = BuildFromConfig(config);
        }

        @Override
        public String call(){
            this.gateway.init();
            this.gateway.start();
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            //mi metto in ascolto della configurazione
            threadedConsumer consumer = new threadedConsumer("cfg-gw_GatewayClient","ConsumerGatewayClient","localhost:29092");
            Future<String> newConfig = Executors.newCachedThreadPool().submit(consumer);

            if (!newConfig.get().isEmpty()){ //aspetto l'invio della configurazione;

                //avvio il produttore con la configurazione arrivata
                threadedProducer producer = new threadedProducer(newConfig.get());
                Future<String> newProducer = Executors.newCachedThreadPool().submit(producer);

                //mi rimetto in ascolto per configurazioni future
                consumer = new threadedConsumer("cfg-gw_GatewayClient","ConsumerGatewayClient","localhost:29092");
                newConfig = Executors.newCachedThreadPool().submit(consumer);

                while (true){
                    if (newConfig.isDone()){//se ho ricevuto nuove configurazioni

                        //stoppo il thread produttore
                        newProducer.cancel(true);

                        //costruisco un nuovo produttore e consumatore
                        producer = new threadedProducer(newConfig.get());
                        consumer = new threadedConsumer("cfg-gw_GatewayClient","ConsumerGatewayClient","localhost:29092");

                        //mi rimetto ad ascoltare per le configurazioni e a produrre
                        newConfig = Executors.newCachedThreadPool().submit(consumer);
                        newProducer = Executors.newCachedThreadPool().submit(producer);

                        System.out.println("CAMBIO CONFIGURAZIONE");
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
    }
    //per creare un produttore per inviare la configurazione -->> SEE Producer.java@main
}