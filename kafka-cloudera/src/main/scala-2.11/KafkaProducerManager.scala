import java.util.HashMap

import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerRecord}

import scala.collection.JavaConversions._

/**
  * Created by Lorenz on 19/04/2016.
  */
class KafkaProducerManager {

  val scalaProps = Map(
    "bootstrap.servers" -> KafkaServer.KAFKA_ADDRESS,
    "acks" -> "all",
    "retries" -> "2",
    "auto.commit.interval.ms" -> "1000",
    "linger.ms" -> "1",
    "block.on.buffer.full" -> "true",
    "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
    "value.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
    "zookeeper.connect" -> KafkaServer.ZOOKEEPER_ADDRESS
  )

  val jMapProps = new HashMap[String, Object](scalaProps)

  val producer: Producer[String, String] = new KafkaProducer[String, String](jMapProps)

  def startCounter() {
    System.out.println("Start Producer Counter")
    for (i <- 0 to 100) {
      producer.send(new ProducerRecord[String, String]("test-counter", i.toString, "Package " + i))
      System.out.println("Producer - Send: " + i)
    }

    System.out.println("Closing producer")
    producer.close()
  }
}
