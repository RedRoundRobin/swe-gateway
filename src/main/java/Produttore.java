import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Produttore {

    void invioDati(String topic, Producer<String, String> produttore, String messaggio) {

        String NOME_TOPIC = topic;
        produttore.send(new ProducerRecord<String, String>(NOME_TOPIC, messaggio));
    }

    String ricevidati() {
        return "";
    }

}
