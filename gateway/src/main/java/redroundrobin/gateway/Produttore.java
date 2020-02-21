package redroundrobin.gateway;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;


import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Produttore {

    private Producer<Long, String> produttore;
    private String bootstrapServers;
    private String nome;

    Produttore(String nome, String bootstrapServers) {
        this.nome = nome;
        this.bootstrapServers = bootstrapServers;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, nome);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.produttore = new KafkaProducer<>(props);
    }

    /*
        Viene eseguito il produttore specificato.
        Il produttore invia il messaggio nel topic specificato.
        N.B. Il produttore viene chiuso a mano
    */
    static void eseguiProduttore(String topic, String messaggio, Produttore produttore) throws Exception {
        System.out.println("Avvio del produttore "+produttore.nome);
        long tempo = System.currentTimeMillis();
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        try {

                final ProducerRecord<Long, String> record = new ProducerRecord<>(topic, tempo, messaggio);
                produttore.produttore.send(record, (metadata, exception) -> {
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

            countDownLatch.await(25, TimeUnit.SECONDS);
        }finally {
            produttore.produttore.flush();
            System.out.println("Messaggio inviato!");
            // produttore.produttore.close();
            // Il produttore andrà chiuso a mano
        }
    }

    void chiudiProduttore(){
        this.produttore.close();
    }


    public static void main(String args[]) throws Exception {

        Produttore test = new Produttore("produttoreTest", "localhost:29092");
        eseguiProduttore("TopicDiProva","Ciao mondo!", test);
        eseguiProduttore("TopicDiProva","Ciao mondo2!", test);
        test.chiudiProduttore();

    }

}
