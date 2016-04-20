import java.util.{HashMap, Properties}

import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerRecord}

import scala.collection.JavaConversions._

/**
  * Created by Lorenz on 19/04/2016.
  */
class KafkaProducerManager {

  val props = new Properties()
  props.put("bootstrap.servers", KafkaServer.KAFKA_ADDRESS)
  props.put("acks", "all")
  props.put("retries", "2")
  props.put("auto.commit.interval.ms", "1000")
  props.put("linger.ms", "1")
  props.put("block.on.buffer.full", "true")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("zookeeper.connect", KafkaServer.ZOOKEEPER_ADDRESS)

  val producer = new KafkaProducer[String, String](props)

  def startCounter() {
    System.out.println("Start Producer Counter")
    for (i <- 0 to 100) {
      producer.send(new ProducerRecord("test-counter", i.toString, "Package " + i))
      System.out.println("Producer - Send: " + i)
    }

    System.out.println("Closing producer")
    producer.close()
  }
}
