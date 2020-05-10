package com.redroundrobin.thirema.gateway.threads;

import com.redroundrobin.thirema.gateway.utils.Consumer;
import java.util.concurrent.Callable;

public class CmdConsumer implements Callable<String> {
  private final Consumer cmdConsumer;

  public CmdConsumer(String topic, String name, String bootstrapServer) {
    this.cmdConsumer = new Consumer(topic, name, bootstrapServer);
  }

  @Override
  public String call() {
    return cmdConsumer.executeConsumer();
  }
}
