import java.util.Properties

import org.apache.kafka.clients.producer.{Producer, ProducerConfig, ProducerRecord}

/**
  * Created by Lorenz on 19/04/2016.
  */
object KafkaProducer {
  val KAFKASERVER = "cl06.ugent.be:9092"
}

class KafkaProducer {
   null

  val props = new Properties()
  props.put("bootstrap.servers", KafkaProducer.KAFKASERVER)
  props.put("acks", "all")
  props.put("retries", 0)
  props.put("auto.commit.interval.ms", 1000)
  props.put("linger.ms", 1)
  props.put("block.on.buffer.full", "true")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val config = new ProducerConfig(props)
  val producer: Producer[String, String] = new KafkaProducer[](props)

  def startCounter(){
    System.out.println("Start Producer Counter")
    for(i <- 0 to 10){
      producer.send(new ProducerRecord[String,String]("test-topic", Integer.toString(i),"Package " + i));
      System.out.println("Producer: Send: " + i);
    }

    System.out.println("Closing producer");
    producer.close();
  }
}
