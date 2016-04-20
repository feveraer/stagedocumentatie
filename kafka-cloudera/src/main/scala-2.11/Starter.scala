/**
  * Created by Lorenz on 20/04/2016.
  */
object ConsumerStarter {
  def main(args: Array[String]) {
    val consumer = new KafkaConsumerManager
    consumer.start()
  }
}

object ProducerStarter {
  def main(args: Array[String]) {
    val producer = new KafkaProducerManager
    producer.startCounter()
  }
}

