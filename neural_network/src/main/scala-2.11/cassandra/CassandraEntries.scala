package cassandra

import org.encog.ml.MLRegression
import org.encog.ml.data.versatile.NormalizationHelper

/**
  * Created by Lorenz on 25/04/2016.
  */
case class SensorLog(sensorId: Int, date: String, time: String, regime: String, measuredTemp: Double, setTemp: Double)

case class SensorInfo(sensorId: Int, location: String, user: String)

case class SensorPrediction(sensorId: Int, date: String, time: String, predictedTemp: Double)

case class SensorModel(sensorId: Int, model: String, normalizer: String)

case class SetTemperatureLog(sensorId: Int, season: String, day: String, hour: Int, quartile: Int, setTemperature: Double)

object SensorModel {
  def build(sensorId: Int, model: MLRegression, normalizer: NormalizationHelper): SensorModel = {
    SensorModel(sensorId, EncogSerializer.serialize(model), EncogSerializer.serialize(normalizer))
  }
}