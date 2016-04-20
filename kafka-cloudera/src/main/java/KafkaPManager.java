import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by Lorenz on 20/04/2016.
 */
public class KafkaPManager {
    private Producer<String, String> producer;
    private static final String KAFKASERVER = "localhost:9092";
//    private static final String ZOOKEEPER = "192.168.22.10:2181";

    public KafkaPManager() {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKASERVER);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("auto.commit.interval.ms", 1000);
        props.put("linger.ms", 1);
        props.put("block.on.buffer.full", "true");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("broker.id", 0);
//        props.put("zookeeper.connect", ZOOKEEPER);

        producer = new KafkaProducer<>(props);
    }

    public void startCounter() {
        System.out.println("Start Producer Counter");
        for (int i = 0; i < 10000; i++) {
            producer.send(new ProducerRecord<>("test", Integer.toString(i), "Package " + i));
            System.out.println("Producer: Send: " + i);
        }

        System.out.println("Closing producer");
        producer.close();
    }

}
