import java.util.{Arrays, HashMap, Properties}

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}

/**
  * Created by Lorenz on 20/04/2016.
  */
class KafkaConsumerManager {
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

  val consumer = new KafkaConsumer[String, String](jMapProps)

  def start() {
    println("Start Consumer")
    consumer.subscribe(Arrays.asList("test-counter-topic"))

    while (true) {
      val records: ConsumerRecords[String, String] = consumer.poll(100)

      val iterator = records.iterator()

      while(iterator.hasNext){
        val record = iterator.next()
        printf("Consumer: offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value())
      }
    }
  }
}
