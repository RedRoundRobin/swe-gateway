package com.redroundrobin.thirema.simulation.extra;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Consumatore {
    private String nome;
    private Consumer<Long, String> consumatore;

    Consumatore(String topic, String nome, String serverBootstrap) {
        this.nome = nome;

        // Imposto le proprietà del consumatore da creare
        final Properties proprieta = new Properties();
        proprieta.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverBootstrap);
        proprieta.put(ConsumerConfig.GROUP_ID_CONFIG, nome);
        proprieta.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        proprieta.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        Consumer<Long, String> consumatore = new KafkaConsumer<>(proprieta);

        // Sottoscrivo il consumatore al topic
        consumatore.subscribe(Collections.singletonList(topic));

        this.consumatore = consumatore;
    }

    // Metodo che esegue un consumatore collegato al topics specificato e che stampa i record trovati, il consumatore continua ad essere attivo
    public void eseguiConsumatore() {
        System.out.println("Consumatore " + nome + " avviato!");

        final int limiteMassimo = 10000000;
        int nessunRecordTrovato = 0;

        while (nessunRecordTrovato < limiteMassimo) {
            final ConsumerRecords<Long, String> recordConsumatore = consumatore.poll(Duration.ofSeconds(1));

            if (recordConsumatore.count() == 0) {
                nessunRecordTrovato++;
            } else {
                recordConsumatore.forEach(record -> System.out.printf("Record del consumatore %s:\t(%d, %s, %d, %d)\n",
                                                                        nome,
                                                                        record.key(),
                                                                        record.value(),
                                                                        record.partition(),
                                                                        record.offset()));

                consumatore.commitAsync();

                System.out.println("Messaggi disponibili consumati!");
            }
        }

        consumatore.close();

        System.out.println("Consumatore " + nome + " chiuso!");
    }

    public static void main(String[] args) {
        Consumatore test = new Consumatore("Aiuto","consumatoreTest", "localhost:29092");
        test.eseguiConsumatore();
    }
}
