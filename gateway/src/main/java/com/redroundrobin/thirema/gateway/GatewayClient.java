package com.redroundrobin.thirema.gateway;

import static com.redroundrobin.thirema.gateway.models.Gateway.buildFromConfig;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.redroundrobin.thirema.gateway.models.Gateway;
import com.redroundrobin.thirema.gateway.utils.Consumer;
import com.redroundrobin.thirema.gateway.utils.CustomLogger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GatewayClient {
  private static final String address = "127.0.1.1";
  private static final int port = 6969;
  private static final String name = "gw_US-GATEWAY-1";

  private static final Logger logger = CustomLogger.getLogger(GatewayClient.class.getName(), Level.FINE);

  public static void main(String[] args) {
    ch.qos.logback.classic.Logger kafkaLogger =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    kafkaLogger.setLevel(ch.qos.logback.classic.Level.OFF);

    try {
      //mi metto in ascolto della configurazione
      CfgThreadedConsumer consumer = new CfgThreadedConsumer("kafka-core:29092");
      Future<String> newConfig = Executors.newCachedThreadPool().submit(consumer);

      //avvio il produttore con la configurazione di default
      ThreadedProducer producer = new ThreadedProducer(consumer.getDEFAULT_CONFIG());
      Future<String> newProducer = Executors.newCachedThreadPool().submit(producer);

      while (true) {
        //se ho ricevuto nuove configurazioni
        if (newConfig.isDone()) {

          //stoppo il thread produttore
          newProducer.cancel(true);

          //costruisco un nuovo produttore e consumatore
          producer = new ThreadedProducer(newConfig.get());
          consumer = new CfgThreadedConsumer("cfg-" + producer.getGatewayName(),
              "cfg-" + producer.getGatewayName(), "kafka-core:29092");

          //mi rimetto ad ascoltare per le configurazioni e a produrre
          newConfig = Executors.newCachedThreadPool().submit(consumer);
          newProducer = Executors.newCachedThreadPool().submit(producer);

          logger.log(Level.CONFIG, "CAMBIO CONFIGURAZIONE");
        }
      }
    } catch (InterruptedException | ExecutionException | IOException e) {
      logger.log(Level.SEVERE, "Interrupted or else!", e);
    }
  }

  private static class CfgThreadedConsumer implements Callable<String> {
    // private static final String DEFAULT_CONFIG = "{\"devices\":  [],  \"maxStoredPackets\":5,  \"maxStoringTime\":6000}";
    private final Consumer consumerConfig;
    private final String DEFAULT_CONFIG;

    public CfgThreadedConsumer(String topic, String name, String bootstrapServer) throws IOException {
      this.DEFAULT_CONFIG = Files.readString(Paths.get("gatewayConfig.json"));
      this.consumerConfig = new Consumer(topic, name, bootstrapServer);
    }

    public CfgThreadedConsumer(String bootstrapServer) throws IOException {

      this.DEFAULT_CONFIG = addFixedPropertiesConfig(
          Files.readString(Paths.get("gatewayConfig.json")));
      Gateway gateway = buildFromConfig(this.DEFAULT_CONFIG);
      this.consumerConfig = new Consumer("cfg-" + gateway.getName(), "cfg-" + gateway.getName(),
          bootstrapServer);
    }

    private String addFixedPropertiesConfig(String defaultConfig) {
      JsonObject jsonObject = new Gson().fromJson(defaultConfig, JsonObject.class);
      jsonObject.addProperty("address", address);
      jsonObject.addProperty("port", port);
      jsonObject.addProperty("name", name);
      return jsonObject.toString();
    }

    public String getDEFAULT_CONFIG() {
      return DEFAULT_CONFIG;
    }

    @Override
    public String call() {
      String newConfig = addFixedPropertiesConfig(consumerConfig.executeConsumer());
      JsonObject jsonObject = new Gson().fromJson(newConfig, JsonObject.class);

      try {
        // used to see if this fields are of type int
        jsonObject.get("maxStoredPackets").getAsInt();
        jsonObject.get("maxStoringTime").getAsInt();
        return newConfig;
      } catch (NumberFormatException e) {
        logger.log(Level.WARNING, "Json parse error!", e);
        return DEFAULT_CONFIG;
      }
    }
  }

  private static class ThreadedProducer implements Callable<String> {
    private final GatewayManager gatewayManager;

    public ThreadedProducer(String config) {
      com.google.gson.JsonObject jsonObject = (new Gson()).fromJson(config, JsonObject.class);
      Gateway gateway = buildFromConfig(config);

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

    public String getGatewayName() {
      return gatewayManager.getName();
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
