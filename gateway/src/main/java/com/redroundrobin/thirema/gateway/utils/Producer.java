package com.redroundrobin.thirema.gateway.utils;

import com.redroundrobin.thirema.gateway.Gateway;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Producer implements AutoCloseable {

    private org.apache.kafka.clients.producer.Producer<Long, String> producer;
    private String name;

    public Producer(String name, String bootstrapServers) {
        this.name = name;

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, name);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        producer = new KafkaProducer<>(properties);
    }

    // Viene eseguito il produttore specificato che invia il messaggio nel topic specificato
    public void executeProducer(String topic, String message) throws Exception {
        System.out.println("Producer " + name + " started!");

        long timestamp = System.currentTimeMillis();
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        try {
            final ProducerRecord<Long, String> record = new ProducerRecord<>(topic, timestamp, message);

            producer.send(record, (metadata, exception) -> {
                long timeSpent = System.currentTimeMillis() - timestamp;

                if (metadata != null) {
                    System.out.printf("Sent record (chiave = %s, valore = %s) with metadata (partizione = %d, offset = %d) and timestamp = %d\n",
                            record.key(),
                            record.value(),
                            metadata.partition(),
                            metadata.offset(),
                            timeSpent);
                } else {
                    Logger logger
                            = Logger.getLogger(
                            Producer.class.getName());

                    // log messages using log(Level level, String msg)
                    logger.log(Level.WARNING, "Error in message sending!", exception);
                }

                countDownLatch.countDown();
            });

            countDownLatch.await(25, TimeUnit.SECONDS);
        } finally {
            producer.flush();
            System.out.println("Message sent!");
        }
    }

    @Override
    public void close() {
        producer.close();
    }

    public static void main(String[] args) throws Exception {
        String conf1 = "{\"address\":\"127.0.1.1\",\n  \"port\":6969,\n  \"name\":\"US-GATEWAY-1\",\n  \"devices\":\n  [{\n    \"deviceId\":1,\n    \"timestamp\":0,\n    \"sensors\":\n    [{\n      \"sensorId\":1,\n      \"timestamp\":0,\n      \"data\":0\n    }]\n  }],\n  \"storedPacket\":5,\n  \"storingTime\":6000}";

        try(Producer configproducer = new Producer("configProducer", "localhost:29092")) {
            configproducer.executeProducer("US-GATEWAY-1-CONFIG", conf1);
        }

    }
}
