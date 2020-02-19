package redroundrobin.gateway;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.Collections;
import java.util.Properties;

import static redroundrobin.gateway.Produttore.eseguiProduttore;

public class Consumatore {
    private final static String BOOTSTRAP_SERVERS = "localhost:29092";
/*
*
* Metodo che crea un consumatore collegato al topic specificato
*
* */
    private static Consumer<Long, String> creaConsumatore(String topic, String nomeConsumatore) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, nomeConsumatore);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // Create the consumer using props.

        final Consumer<Long, String> consumatore = new KafkaConsumer<>(props);
        // Subscribe to the topic.
        consumatore.subscribe(Collections.singletonList(topic));

        return consumatore;
    }
/*
*
* Metodo che esegue un consumatore collegato al topics specificato e che stampa i record trovati
* Il consumatore continua ad essere attivo
*
* */
    static void eseguiConsumatore(String topic, String nomeConsumatore) throws InterruptedException {
        System.out.println("Avvio del consumatore...");
        final Consumer<Long, String> consumatore = creaConsumatore(topic, nomeConsumatore);
        final int maxCap = 100;
        int nessunRecordTrovato = 0;

        while (true) {
            final ConsumerRecords<Long, String> recordConsumatore = consumatore.poll(1000);
            if (recordConsumatore.count()==0) {
                nessunRecordTrovato++;
                if (nessunRecordTrovato > maxCap) break;
                else continue;
            }
            recordConsumatore.forEach(record -> {
                System.out.printf("Record del consumatore %s: \t(%d, %s, %d, %d)\n",
                        nomeConsumatore,
                        record.key(), record.value(),
                        record.partition(), record.offset());
            });
            consumatore.commitAsync();

        }
        consumatore.close();
        System.out.println("Messaggi consumati!");
    }

    public static void main(String args[]) throws Exception {

        Consumatore.eseguiConsumatore("TopicDiProva", "ConsumatoreDiProva");

    }
}
