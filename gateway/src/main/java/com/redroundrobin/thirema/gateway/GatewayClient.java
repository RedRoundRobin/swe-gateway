package com.redroundrobin.thirema.gateway;

import static com.redroundrobin.thirema.gateway.Gateway.buildFromConfig;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.utils.Consumer;
import com.redroundrobin.thirema.gateway.utils.CustomLogger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GatewayClient {
  private static final Logger logger = CustomLogger.getLogger(GatewayClient.class.getName());

  public static void main(String[] args) {
    try {
      //mi metto in ascolto della configurazione
      ThreadedConsumer consumer = new ThreadedConsumer("cfg-gw_GatewayClient", "ConsumerGatewayClient", "localhost:29092");
      Future<String> newConfig = Executors.newCachedThreadPool().submit(consumer);

      //avvio il produttore con la configurazione di default
      ThreadedProducer producer = new ThreadedProducer(consumer.getDEFAULT_CONFIG());
      Future<String> newProducer = Executors.newCachedThreadPool().submit(producer);

      //aspetto l'invio della configurazione
      /*if (!newConfig.get().isEmpty()) {

        //avvio il produttore con la configurazione arrivata
        ThreadedProducer producer = new ThreadedProducer(newConfig.get());
        Future<String> newProducer = Executors.newCachedThreadPool().submit(producer);

        //mi rimetto in ascolto per configurazioni future
        consumer = new ThreadedConsumer("cfg-gw_GatewayClient", "ConsumerGatewayClient", "localhost:29092");
        newConfig = Executors.newCachedThreadPool().submit(consumer);*/

        while (true) {
          //se ho ricevuto nuove configurazioni
          if (newConfig.isDone()) {

            //stoppo il thread produttore
            newProducer.cancel(true);

            //costruisco un nuovo produttore e consumatore
            producer = new ThreadedProducer(newConfig.get());
            consumer = new ThreadedConsumer("cfg-gw_GatewayClient", "ConsumerGatewayClient", "localhost:29092");

            //mi rimetto ad ascoltare per le configurazioni e a produrre
            newConfig = Executors.newCachedThreadPool().submit(consumer);
            newProducer = Executors.newCachedThreadPool().submit(producer);

            logger.log(Level.CONFIG, "CAMBIO CONFIGURAZIONE");
          }
        }
      //}
    } catch (InterruptedException | ExecutionException | IOException e) {
      logger.log(Level.SEVERE, "Interrupted or else!", e);
    }
  }

  private static class ThreadedConsumer implements Callable<String> {
    //private static final String DEFAULT_CONFIG = "{\"address\":\"127.0.1.1\",\"port\":6969,\"name\":\"US-GATEWAY-1\",  \"devices\":  [],  \"storedPacket\":5,  \"storingTime\":6000}";
    private final Consumer consumerConfig;
    private final String DEFAULT_CONFIG;

    public ThreadedConsumer(String topic, String name, String bootstrapServer) throws IOException {
      this.DEFAULT_CONFIG = Files.readString(Paths.get("gatewayConfig.json"));
      this.consumerConfig = new Consumer(topic, name, bootstrapServer);
    }

    public String getDEFAULT_CONFIG() {
      return DEFAULT_CONFIG;
    }

    @Override
    public String call() {
      String newConfig = consumerConfig.executeConsumer();
      Gson gson = new Gson();
      JsonParser parser = new JsonParser();
      JsonObject obj = parser.parse(newConfig).getAsJsonObject();
      int port;

      String address = gson.fromJson(obj.get("address"), String.class);
      port = gson.fromJson(obj.get("port"), int.class);
      String name = gson.fromJson(obj.get("name"), String.class);
      List<Device> devices = new ArrayList<>();

      gson.fromJson(obj.get("devices"), JsonArray.class).forEach(dev -> {
        Device device = gson.fromJson(dev, Device.class);
        devices.add(device);
      });

      if (address.isEmpty() || port <= -1 || name.isEmpty()) {
        return DEFAULT_CONFIG;
      }
      return newConfig;
    }
  }

  private static class ThreadedProducer implements Callable<String> {
    private final Gateway gateway;

    public ThreadedProducer(String config) {
      this.gateway = buildFromConfig(config);
    }

    @Override
    public String call() {
      this.gateway.init();
      this.gateway.start();
      return null;
    }
  }
  //per creare un produttore per inviare la configurazione -->> SEE Producer.java@main
}
