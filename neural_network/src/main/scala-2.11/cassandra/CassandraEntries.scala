package cassandra

/**
  * Created by Lorenz on 25/04/2016.
  */
case class SensorLog(sensorId: Int, date: String, time: String, regime: String, measuredTemp: Double, setTemp: Double)

case class SensorInfo(sensorId: Int, location: String, user: String)

case class SensorPrediction(sensorId: Int, date: String, time: String, predictedTemp: Double)