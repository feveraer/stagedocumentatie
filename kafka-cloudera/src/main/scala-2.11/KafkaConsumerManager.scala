import java.util.{Arrays, HashMap}

import scala.collection.JavaConversions._

import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}

/**
  * Created by Lorenz on 20/04/2016.
  */
class KafkaConsumerManager {
  val scalaProps = Map(
    "bootstrap.servers" -> KafkaServer.KAFKA_ADDRESS,
    "metadata.broker.list" -> KafkaServer.KAFKA_ADDRESS,
    "group.id" -> "testGroup",
    "enable.auto.commit" -> "true",
    "auto.commit.interval.ms" -> "1000",
    "linger.ms" -> "1",
    "session.timeout.ms" -> "30000",
    "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
    "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
    "zookeeper.connect" -> KafkaServer.ZOOKEEPER_ADDRESS
  )

  val jMapProps = new HashMap[String, Object](scalaProps)

  val consumer = new KafkaConsumer[String, String](jMapProps)

  def start() {
    println("Start Consumer")
    consumer.subscribe(Arrays.asList("test-counter"))

    while (true) {
      val records: ConsumerRecords[String, String] = consumer.poll(100)

      val iterator = records.iterator()

      while (iterator.hasNext) {
        val record = iterator.next()
        printf("Consumer: offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value())
      }
    }
  }
}
