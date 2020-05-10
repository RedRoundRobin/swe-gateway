package com.redroundrobin.thirema.gateway.threads;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.redroundrobin.thirema.gateway.GatewayManager;
import com.redroundrobin.thirema.gateway.models.Gateway;
import java.util.concurrent.Callable;

import static com.redroundrobin.thirema.gateway.models.Gateway.buildFromConfig;

public class DataProducer implements Callable<String> {
  private final GatewayManager gatewayManager;

  public DataProducer(String config) {
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
