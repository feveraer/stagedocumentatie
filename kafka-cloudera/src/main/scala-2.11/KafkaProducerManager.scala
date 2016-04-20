import java.util.HashMap

import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerRecord}

import scala.collection.JavaConversions._

/**
  * Created by Lorenz on 19/04/2016.
  */
class KafkaProducerManager {

  val scalaProps = Map(
    "bootstrap.servers" -> KafkaServer.ADDRESS,
    "acks" -> "all",
    "retries" -> 0.toString,
    "auto.commit.interval.ms" -> 1000.toString,
    "linger.ms" -> 1.toString,
    "block.on.buffer.full" -> "true",
    "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
    "value.serializer" -> "org.apache.kafka.common.serialization.StringSerializer"
  )

  val jMapProps = new HashMap[String, Object](scalaProps)

  val producer: Producer[String, String] = new KafkaProducer[String, String](jMapProps)

  def startCounter() {
    System.out.println("Start Producer Counter")
    for (i <- 0 to 10) {
      producer.send(new ProducerRecord[String, String]("test-counter-topic", Integer.toString(i), "Package " + i));
      System.out.println("Producer: Send: " + i);
    }

    System.out.println("Closing producer");
    producer.close();
  }
}
