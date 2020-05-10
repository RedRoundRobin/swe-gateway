package com.redroundrobin.thirema.gateway.threads;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.redroundrobin.thirema.gateway.utils.Consumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public class CfgConsumer implements Callable<String> {
  // private static final String DEFAULT_CONFIG = "{\"devices\":  [],  \"maxStoredPackets\":5,  \"maxStoringTime\":6000}";
  private final Consumer consumerConfig;
  private final String DEFAULT_CONFIG;

  private final String gatewayName;
  private final String address;
  private final int port;

  public CfgConsumer(String bootstrapServer, String gatewayName, String address, int port) throws IOException {
    this.gatewayName = gatewayName;
    this.address = address;
    this.port = port;

    this.DEFAULT_CONFIG = addFixedPropertiesConfig(
        Files.readString(Paths.get("gatewayConfig.json")));
    this.consumerConfig = new Consumer("cfg-" + gatewayName, "cfg-" + gatewayName, bootstrapServer);
  }

  private String addFixedPropertiesConfig(String defaultConfig) {
    JsonObject jsonObject = new Gson().fromJson(defaultConfig, JsonObject.class);
    jsonObject.addProperty("name", gatewayName);
    jsonObject.addProperty("address", address);
    jsonObject.addProperty("port", port);
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
      return DEFAULT_CONFIG;
    }
  }
}
