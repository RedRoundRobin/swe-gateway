package redroundrobin.gateway;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import java.util.Properties;

public class Gateway {

    static Producer<String, String> connessioneKafka(String serverHost) {

        // Configurazione impostazioni Kafka
        String SERVER_HOST = serverHost;

        // inizializzazione della connessione con Kafka
        Properties props = new Properties();
        props.put("bootstrap.servers", SERVER_HOST);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        return new KafkaProducer(props);
    }
}
