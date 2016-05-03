package workers

import akka.actor.Actor
import ann.{Constants, NeuralNetwork}
import cassandra.{CassandraConnection, SensorLog}
import org.encog.ml.MLRegression
import org.encog.ml.data.versatile.NormalizationHelper

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
    var model: (NormalizationHelper, MLRegression) = null
    try {
      model = CassandraConnection.getANNModelsForOutput(sensorLog.sensorId)
    } catch {
      case e: RuntimeException => {
        model = CassandraConnection.getANNModelsForOutput(Constants.DEFAULT_USER_ID)
      }
    }
    ann.loadModel(model._1, model._2)
    println("Prediction test for next temperature")
    val output = ann.predict(sensorLog)
    println("Output: " + output)
  }
}
