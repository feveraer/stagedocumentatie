import java.util.{Arrays, Properties}

import org.apache.kafka.clients.consumer.KafkaConsumer

/**
  * Created by Lorenz on 20/04/2016.
  */
class KafkaConsumerManager {

  val props = new Properties()
  props.put("bootstrap.servers", KafkaServer.KAFKA_ADDRESS)
  props.put("group.id", "testGroup")
  props.put("enable.auto.commit", "true")
  props.put("auto.commit.interval.ms", "1000")
  props.put("linger.ms", "1")
  props.put("session.timeout.ms", "3000")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("zookeeper.connect", KafkaServer.ZOOKEEPER_ADDRESS)

  val consumer = new KafkaConsumer[String, String](props)

  def start() {
    println("Start Consumer")
    consumer.subscribe(Arrays.asList("test-counter"))

    while (true) {
      val records = consumer.poll(100)

      val iterator = records.iterator()

      while (iterator.hasNext) {
        val record = iterator.next()
        printf("Consumer: offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value())
      }
    }
  }
}
