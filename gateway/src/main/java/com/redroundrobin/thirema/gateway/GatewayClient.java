package com.redroundrobin.thirema.gateway;

import static com.redroundrobin.thirema.gateway.models.Gateway.buildFromConfig;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Gateway;
import com.redroundrobin.thirema.gateway.utils.Consumer;
import com.redroundrobin.thirema.gateway.utils.CustomLogger;
import org.slf4j.LoggerFactory;

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
  private static final Logger logger = CustomLogger.getLogger(GatewayClient.class.getName(), Level.FINE);

  public static void main(String[] args) {
    ch.qos.logback.classic.Logger kafkaLogger =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    kafkaLogger.setLevel(ch.qos.logback.classic.Level.OFF);

    try {
      //mi metto in ascolto della configurazione
      ThreadedConsumer consumer = new ThreadedConsumer("cfg-gw_GatewayClient", "ConsumerGatewayClient", "kafka-core:29092");
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
        consumer = new ThreadedConsumer("cfg-gw_GatewayClient", "ConsumerGatewayClient", "kafka-core:29092");
        newConfig = Executors.newCachedThreadPool().submit(consumer);*/

      while (true) {
        //se ho ricevuto nuove configurazioni
        if (newConfig.isDone()) {

          //stoppo il thread produttore
          newProducer.cancel(true);

          //costruisco un nuovo produttore e consumatore
          producer = new ThreadedProducer(newConfig.get());
          consumer = new ThreadedConsumer("cfg-gw_GatewayClient", "ConsumerGatewayClient", "kafka-core:29092");

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
    // private static final String DEFAULT_CONFIG = "{\"address\":\"127.0.1.1\",\"port\":6969,\"name\":\"US-GATEWAY-1\",  \"devices\":  [],  \"storedPacket\":5,  \"storingTime\":6000}";
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

      if (address.isEmpty() || port <= -1 || name.isEmpty()) {
        return DEFAULT_CONFIG;
      }
      return newConfig;
    }
  }

  private static class ThreadedProducer implements Callable<String> {
    private final GatewayManager gatewayManager;

    public ThreadedProducer(String config) {
      Gateway gateway = buildFromConfig(config);
      com.google.gson.JsonObject jsonObject = (new Gson()).fromJson(config, JsonObject.class);

      int maxStoredPackets = 10;
      int maxStoringTime = 10;
      if (jsonObject.has("maxStoredPackets")) {
        maxStoredPackets = jsonObject.get("maxStoredPackets").getAsInt();
      }
      if (jsonObject.has("maxStoringTime")) {
        maxStoringTime = jsonObject.get("maxStoringTime").getAsInt();
      }
      logger.log(Level.FINE, "MaxStoredPackets: " + maxStoredPackets);
      logger.log(Level.FINE, "MaxStoringTime: " + maxStoringTime);

      this.gatewayManager = new GatewayManager(gateway, maxStoredPackets, maxStoringTime);
    }

    @Override
    public String call() {
      this.gatewayManager.init();
      this.gatewayManager.start();
      return null;
    }
  }
  //per creare un produttore per inviare la configurazione -->> SEE Producer.java@main
}
