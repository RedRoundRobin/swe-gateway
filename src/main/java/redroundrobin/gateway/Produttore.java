package redroundrobin.gateway;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;


import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Produttore {

    private final static String BOOTSTRAP_SERVERS = "localhost:29092";
    /*
    * Crea un produttore con le proprietà specificate nel corpo del metodo, connesso ai server BOOTSTRAP_SERVERS
    * */
    private static Producer<Long, String> creaProduttore() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
    /*
    * Esegue il produttore che produce "numeroMessaggi" messaggi nel topic specificato e li stampa nell'output standard
    * NB: in una implementazione futura sarà da sostituire l'invio di messaggi ripetuti con un'arrayList di messaggi
    * */
    static void eseguiProduttore(String topic, final int numeroMessaggi) throws Exception {
        System.out.println("Avvio del produttore...");
        final Producer<Long, String> produttore = creaProduttore();
        long tempo = System.currentTimeMillis();
        final CountDownLatch countDownLatch = new CountDownLatch(numeroMessaggi);

        try {
            for (long index = tempo; index < tempo + numeroMessaggi; index++) {
                final ProducerRecord<Long, String> record = new ProducerRecord<>(topic, index, "This is some serious gourmet sh*t " + index);
                produttore.send(record, (metadata, exception) -> {
                    long tempoTrascorso = System.currentTimeMillis() - tempo;
                    if (metadata != null) {
                        System.out.printf("Inviato il record(chiave=%s valore=%s) " +
                                        "meta(partizione=%d, offset=%d) tempo=%d\n",
                                record.key(), record.value(), metadata.partition(),
                                metadata.offset(), tempoTrascorso);
                    } else {
                        exception.printStackTrace();
                    }
                    countDownLatch.countDown();
                });
            }
            countDownLatch.await(25, TimeUnit.SECONDS);
        }finally {
            produttore.flush();
            System.out.println("Messaggi inviati!");
            produttore.close();
        }
    }

/*
* Metodo che restituisce i dati prodotti da un dispositivo*/
    String ricevidati() {
        return "";
    }

    public static void main(String args[]) throws Exception {

        eseguiProduttore("TopicDiProva",15);

    }

}
