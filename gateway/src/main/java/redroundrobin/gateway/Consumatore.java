package redroundrobin.gateway;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.Collections;
import java.util.Properties;

import static redroundrobin.gateway.Produttore.eseguiProduttore;

public class Consumatore {
    private String topic;
    private String bootstrapServers; //Lista di indirizzoIP:porta separati da una virgola.
    private String nome;
    private Consumer<Long, String> consumatore;


    Consumatore(String topic, String nomeConsumatore, String boostrapServers) {
        this.topic = topic;
        this.bootstrapServers = boostrapServers;
        this.nome = nomeConsumatore;

        //Imposto le proprietà del consumatore da creare
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, nomeConsumatore);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());


        Consumer<Long, String> cons = new KafkaConsumer<>(props);

        //Sottoscrivo il consumatore al topic
        cons.subscribe(Collections.singletonList(topic));

        this.consumatore = cons;
    }


/*
*
* Metodo che esegue un consumatore collegato al topics specificato e che stampa i record trovati
* Il consumatore continua ad essere attivo
*
* */
    static void eseguiConsumatore(Consumatore consumatore) throws InterruptedException {
        System.out.println("Consumatore "+consumatore.nome+" avviato");
        final int maxCap = 100;
        int nessunRecordTrovato = 0;

        while (true) {
            final ConsumerRecords<Long, String> recordConsumatore = consumatore.consumatore.poll(1000);
            if (recordConsumatore.count()==0) {
                nessunRecordTrovato++;
                if (nessunRecordTrovato > maxCap) break;
                else continue;
            }
            recordConsumatore.forEach(record -> {
                System.out.printf("Record del consumatore %s: \t(%d, %s, %d, %d)\n",
                        consumatore.nome,
                        record.key(), record.value(),
                        record.partition(), record.offset());
            });
            consumatore.consumatore.commitAsync();
            System.out.println("Messaggi disponibili consumati!");

        }
        consumatore.consumatore.close();
        System.out.println("Consumatore "+consumatore.nome+" chiuso");
    }

    public static void main(String args[]) throws Exception {

        Consumatore test = new Consumatore("TopicDiProva","consumatoreTest", "localhost:29092");
        Consumatore.eseguiConsumatore(test);

    }
}
