package com.redroundrobin.thirema.gateway.utils;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class Consumer {
  private final String name;
  private final org.apache.kafka.clients.consumer.Consumer<Long, String> kafkaConsumer;

  private static final Logger logger = Logger.getLogger(Consumer.class.getName());

  public Consumer(String topic, String name, String bootstrapServers) {
    this.name = name;

    // Imposto le proprietà del consumatore da creare
    final Properties properties = new Properties();
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, name);
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

    kafkaConsumer = new KafkaConsumer<>(properties);

    // Sottoscrivo il consumatore al topic
    kafkaConsumer.subscribe(Collections.singletonList(topic));
  }

  // Metodo che esegue un consumatore collegato al topics specificato e che stampa i record trovati, il consumatore continua ad essere attivo
  public String executeConsumer() {
    logger.log(Level.INFO, () -> "Consumer " + name + " started!");

    String jsonReceived = "";
    boolean found = false;

    while (!found) {
      final ConsumerRecords<Long, String> records = kafkaConsumer.poll(Duration.ofSeconds(1));

      if (!records.isEmpty()) {
        found = true;
        for (ConsumerRecord<Long, String> record : records) {
          jsonReceived = record.value();
        }
      }
    }

    kafkaConsumer.commitAsync();
    kafkaConsumer.close();

    logger.log(Level.INFO, () -> "Consumer " + name + " closed!");
    return jsonReceived;
  }

  public static void main(String[] args) {
    Consumer test = new Consumer("US-GATEWAY-1", "consumatoreTest", "kafka-core:29092");
    test.executeConsumer();
  }
}
