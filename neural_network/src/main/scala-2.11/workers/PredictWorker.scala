package workers

import akka.actor.Actor
import akka.actor.Actor.Receive
import akka.util.ByteString
import ann.{Constants, NeuralNetwork}
import cassandra.SensorLog
import connections.Connections

/**
  * Created by Frederic on 2/05/2016.
  */
class PredictWorker extends Actor{

  override def receive = {
    case sensorLog: SensorLog => {
      predict(sensorLog)
      context.stop(self)
    }
  }

  def predict(sensorLog: SensorLog) {
    val ann = new NeuralNetwork
    ann.loadModel(
      Constants.RESOURCES_PATH + Constants.ENCOG_NORMALIZATION_HELPER_PATH,
      Constants.RESOURCES_PATH + Constants.ENCOG_BEST_METHOD_PATH)

    println("Prediction test for next temperature")
    val output = ann.predict(sensorLog)
    println("Output: " + output)
  }
}
