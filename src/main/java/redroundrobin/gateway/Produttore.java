package redroundrobin.gateway;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Produttore {

    static void invioDati(String nomeTopic, Producer<String, String> produttore, String messaggio) {

        produttore.send(new ProducerRecord<String, String>(nomeTopic, messaggio));
    }

    String ricevidati() {
        return "";
    }

}
