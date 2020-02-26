package com.redroundrobin.thirema.gateway.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Produttore implements AutoCloseable {

    private Producer<Long, String> produttore;
    private String nome;

    public Produttore(String nome, String serverBootstrap) {
        this.nome = nome;

        Properties proprieta = new Properties();
        proprieta.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverBootstrap);
        proprieta.put(ProducerConfig.CLIENT_ID_CONFIG, nome);
        proprieta.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        proprieta.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        produttore = new KafkaProducer<>(proprieta);
    }

    // Viene eseguito il produttore specificato che invia il messaggio nel topic specificato
    public void eseguiProduttore(String topic, String messaggio) throws Exception {
        System.out.println("Avvio del produttore " + nome);

        long tempo = System.currentTimeMillis();
        final CountDownLatch contoAllaRovescia = new CountDownLatch(1);

        try {
            final ProducerRecord<Long, String> record = new ProducerRecord<>(topic, tempo, messaggio);

            produttore.send(record, (metadati, eccezione) -> {
                long tempoTrascorso = System.currentTimeMillis() - tempo;

                if (metadati != null) {
                    System.out.printf("Inviato il record (chiave = %s, valore = %s) con meta (partizione = %d, offset = %d) e tempo = %d\n",
                            record.key(),
                            record.value(),
                            metadati.partition(),
                            metadati.offset(),
                            tempo);
                } else {
                    eccezione.printStackTrace();
                }

                contoAllaRovescia.countDown();
            });

            contoAllaRovescia.await(25, TimeUnit.SECONDS);
        } finally {
            produttore.flush();
            System.out.println("Messaggio inviato!");
        }
    }

    @Override
    public void close() {
        produttore.close();
    }

    public static void main(String[] args) throws Exception {
        Produttore test = new Produttore("produttoreTest", "localhost:29092");
        while(true) {
            test.eseguiProduttore("TopicDiProva", "Ciao mondo!");
        }

    }
}
