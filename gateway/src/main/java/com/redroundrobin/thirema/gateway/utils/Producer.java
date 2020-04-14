package com.redroundrobin.thirema.gateway.utils;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer implements AutoCloseable {
  private final String name;
  private final org.apache.kafka.clients.producer.Producer<Long, String> kafkaProducer;

  private static final Logger logger = Logger.getLogger(Producer.class.getName());

  public Producer(String name, String bootstrapServers) {
    this.name = name;

    Properties properties = new Properties();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.put(ProducerConfig.CLIENT_ID_CONFIG, name);
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    kafkaProducer = new KafkaProducer<>(properties);
  }

  // Viene eseguito il produttore specificato che invia il messaggio nel topic specificato
  public void executeProducer(String topic, String message) throws InterruptedException {
    logger.log(Level.INFO, () -> "Producer " + name + " started!");

    long timestamp = System.currentTimeMillis();
    final CountDownLatch countDownLatch = new CountDownLatch(1);

    try {
      final ProducerRecord<Long, String> record = new ProducerRecord<>(topic, timestamp, message);

      kafkaProducer.send(record, (metadata, exception) -> {
        long timeSpent = System.currentTimeMillis() - timestamp;

        if (metadata != null) {
          logger.log(Level.INFO, () -> "Sent record (chiave = " + record.key() + ", valore = " + record.value() + ") with metadata (partizione = " + metadata.partition() + ", offset = " + metadata.offset() + ") and timestamp = " + timeSpent + " %n");
        } else {
          logger.log(Level.WARNING, "Error in message sending!", exception);
        }

        countDownLatch.countDown();
      });

      countDownLatch.await(25, TimeUnit.SECONDS);
    } finally {
      kafkaProducer.flush();
      logger.log(Level.INFO, "Message sent!");
    }
  }

  @Override
  public void close() {
    kafkaProducer.close();
  }

  public static void main(String[] args) throws Exception {
    String conf1 = "{\"address\":\"127.0.1.1\",\n  \"port\":6969,\n  \"name\":\"US-GATEWAY-1\",\n  \"devices\":\n  [{\n    \"deviceId\":1,\n    \"timestamp\":0,\n    \"sensors\":\n    [{\n      \"sensorId\":1,\n      \"timestamp\":0,\n      \"data\":0\n    }]\n  }],\n  \"storedPacket\":5,\n  \"storingTime\":6000}";

    try (Producer configProducer = new Producer("configProducer", "kafka_core:29092")) {
      configProducer.executeProducer("US-GATEWAY-1-CONFIG", conf1);
    }

  }
}
