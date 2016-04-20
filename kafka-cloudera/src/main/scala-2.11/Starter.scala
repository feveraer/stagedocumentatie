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

object CombinedStarter {
  def main(args: Array[String]) {
    val kafkaTunnel = new SSHTunnel
    kafkaTunnel.connect("root", "Ugent2012", "cloudera.ugent.be", "cl06.ugent.be", 9092, 9092)

    val zooKeeperTunnel = new SSHTunnel
    zooKeeperTunnel.connect("root", "Ugent2012", "cloudera.ugent.be", "cl02.ugent.be", 2181, 2181)

    val consumerThread = new Thread(new Runnable {
      override def run(): Unit = {
        val consumer = new KafkaConsumerManager
        consumer.start()
      }
    })

    val producerThread = new Thread(new Runnable {
      override def run(): Unit ={
        val producer = new KafkaProducerManager
        producer.startCounter()
      }
    })

    consumerThread.start()
    producerThread.start()
  }
}

