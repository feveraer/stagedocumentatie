/**
  * Created by vagrant on 31.03.16.
  */
object Runner {
  def main(args: Array[String]) {
    val consumer = new Consumer()
    consumer.createConsumer()
    consumer.startConsumingTopic("test")
  }
}
