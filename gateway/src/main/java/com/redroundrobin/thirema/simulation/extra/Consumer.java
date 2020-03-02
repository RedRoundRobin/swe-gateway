package com.redroundrobin.thirema.simulation.extra;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Consumer {
    private String name;
    private org.apache.kafka.clients.consumer.Consumer<Long, String> consumer;

    Consumer(String topic, String name, String bootstrapServers) {
        this.name = name;

        // Imposto le proprietà del consumatore da creare
        final Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, name);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        org.apache.kafka.clients.consumer.Consumer<Long, String> consumer = new KafkaConsumer<>(properties);

        // Sottoscrivo il consumatore al topic
        consumer.subscribe(Collections.singletonList(topic));

        this.consumer = consumer;
    }

    // Metodo che esegue un consumatore collegato al topics specificato e che stampa i record trovati, il consumatore continua ad essere attivo
    public void executeConsumer() {
        System.out.println("Consumer " + name + " started!");

        final int recordLimit = 10000000;
        int recordNotFound = 0;

        while (recordNotFound < recordLimit) {
            final ConsumerRecords<Long, String> records = consumer.poll(Duration.ofSeconds(1));

            if (records.count() == 0) {
                recordNotFound++;
            } else {
                records.forEach(record -> System.out.printf("Record of consumer %s:\t(%d, %s, %d, %d)\n",
                        name,
                        record.key(),
                        record.value(),
                        record.partition(),
                        record.offset()));

                consumer.commitAsync();

                System.out.println("Available messages consumed!");
            }
        }

        consumer.close();

        System.out.println("Consumer " + name + " closed!");
    }

    public static void main(String[] args) {
        Consumer test = new Consumer("Aiuto","consumatoreTest", "localhost:29092");
        test.executeConsumer();
    }
}
