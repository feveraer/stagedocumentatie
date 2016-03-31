import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer

/**
  * Created by vagrant on 31.03.16.
  */
class Consumer {
  val KAFKA_ADDRESS = "192.168.22.10:9092"

  var consumer: KafkaConsumer[String, String] = null

  val keyspace = "testdata"

  def createConsumer() {
    val props = new Properties()
    props.put("bootstrap.servers", KAFKA_ADDRESS)
    props.put("group.id", "testGroup")
    props.put("enable.auto.commit", "true")
    props.put("auto.commit.interval.ms", "1000")
    props.put("session.timeout.ms", "30000")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    consumer = new KafkaConsumer(props)
  }

  def startConsumingTopic(topic: String) {
    println("Setting up cassandra")

    val cassandra = new CassandraConnection()
    cassandraSetup(cassandra)

    println("Start Consumer for topic: " + topic)

    consumer.subscribe(util.Arrays.asList("test"))
    while (true) {
      val records = consumer.poll(100)
      val recordIterator = records.iterator()

      while(recordIterator.hasNext) {
        val record = recordIterator.next()
        printf("Consumer: offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value())
        val statement = "INSERT INTO " + keyspace + ".kafka (key, message) VALUES (" + record.key() + ", '" + record.value() +"')"
        cassandra.executeQuery(statement)
      }
    }
  }

  private def cassandraSetup(cassandra: CassandraConnection) {
    cassandra.connect()
    cassandra.executeQuery("CREATE TABLE IF NOT EXISTS kafka("
      + "key int, "
      + "message text, "
      + "PRIMARY KEY (key) "
      + ")"
    )
    // empty table from previous runs
    cassandra.executeQuery("TRUNCATE TABLE kafka")
  }

}
