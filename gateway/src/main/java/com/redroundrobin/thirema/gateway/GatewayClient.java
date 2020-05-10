package com.redroundrobin.thirema.gateway;

import com.redroundrobin.thirema.gateway.threads.CfgConsumer;
import com.redroundrobin.thirema.gateway.threads.DataProducer;
import com.redroundrobin.thirema.gateway.utils.CustomLogger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GatewayClient {
  private static final String ADDRESS = "127.0.1.1";
  private static final int PORT = 6969;
  private static final String GATEWAY_NAME = "gw_US-GATEWAY-1";
  private static final String BOOTSTRAP_SERVER = "kafka-core:29092";

  private static final Logger logger = CustomLogger.getLogger(GatewayClient.class.getName(),
      Level.FINE);

  public static void main(String[] args) {
    ch.qos.logback.classic.Logger kafkaLogger =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    kafkaLogger.setLevel(ch.qos.logback.classic.Level.OFF);

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    Future<String> newConfig = null;
    Future<String> newProducer = null;

    try {
      //mi metto in ascolto della configurazione
      CfgConsumer consumer = new CfgConsumer(BOOTSTRAP_SERVER, GATEWAY_NAME, ADDRESS, PORT);
      newConfig = executorService.submit(consumer);

      //avvio il produttore con la configurazione di default
      DataProducer producer = new DataProducer(consumer.getDEFAULT_CONFIG());
      newProducer = executorService.submit(producer);

      while (true) {
        
        Thread.sleep(1000);
        
        //se ho ricevuto nuove configurazioni
        if (newConfig.isDone()) {

          //stoppo il thread produttore
          newProducer.cancel(true);
          //executorService.shutdown();

          //costruisco un nuovo produttore e consumatore
          producer = new DataProducer(newConfig.get());
          consumer = new CfgConsumer(BOOTSTRAP_SERVER, producer.getGatewayName(), ADDRESS, PORT);

          //mi rimetto ad ascoltare per le configurazioni e a produrre
          newConfig = executorService.submit(consumer);
          newProducer = executorService.submit(producer);

          logger.log(Level.CONFIG, "CAMBIO CONFIGURAZIONE");
        }
      }
    } catch (InterruptedException | ExecutionException | IOException e) {
      logger.log(Level.SEVERE, "Interrupted or else!", e);
    }

    if (newConfig != null) {
      newConfig.cancel(true);
    }
    if (newProducer != null) {
      newProducer.cancel(true);
    }
    executorService.shutdownNow();
  }

  //per creare un produttore per inviare la configurazione -->> SEE Producer.java@main
}
