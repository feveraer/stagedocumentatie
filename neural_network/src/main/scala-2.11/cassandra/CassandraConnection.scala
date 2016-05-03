package cassandra

import com.datastax.driver.core.{Cluster, ResultSet, Session}
import connections.SSHTunnel
import org.encog.ml.MLRegression
import org.encog.ml.data.versatile.NormalizationHelper


/**
  * Created by Lorenz on 31.03.16.
  */
object CassandraConnection {
  // config variables
  val keyspace = "qbus"

  var cluster: Cluster = null
  var session: Session = null

  // create connection to cluster
  def connect(): Session = {
    cluster = Cluster.builder().addContactPoint("localhost").withPort(SSHTunnel.lportCassandra).build()
    val metadata = cluster.getMetadata
    printf("Connected to cluster: %s\n", metadata.getClusterName)

    session = cluster.connect(keyspace)
    session
  }

  def getSession: Session = {
    if (session != null) {
      session
    } else {
      connect()
      session
    }
  }

  def executeQuery(cqlStatement: String): Option[ResultSet] = {
    try {
      Option(session.execute(cqlStatement))
    }
    catch {
      case e: Exception =>
        println(e.getMessage)
        None
    }
  }

  // close connection with Cassandra cluster
  def close() {
    if (cluster != null) {
      cluster.close()
      println("Connection closed")
    }
  }

  /*
   * INSERTS
   */

  def insertSensorLog(log: SensorLog): Unit = {
    checkSession

    val cqlStatement =
      "INSERT INTO " + keyspace + ".sensor_logs (outputid, date, time, regime, measuredtemperature, settemperature) " +
        "VALUES(" + log.sensorId + ",'" + log.date + "','" + log.time + "','" + log.regime + "'," +
        log.measuredTemp + "," + log.setTemp + ");"

    executeQuery(cqlStatement)

    //    println("Log inserted")
  }

  def insertSensorInfo(info: SensorInfo): Unit = {
    checkSession

    val cqlStatement =
      "INSERT INTO " + keyspace + ".sensor_info (outputid, location, user) " +
        "VALUES(" + info.sensorId + ",'" + info.location + "','" + info.user + "')" +
        "IF NOT EXISTS;"

    executeQuery(cqlStatement)

    //    println("Info inserted")
  }

  def insertSensorPrediction(prediction: SensorPrediction): Unit = {
    checkSession

    val cqlStatement =
      "INSERT INTO " + keyspace + ".sensor_predictions(outputid, date, time, prediction) " +
        "VALUES (" + prediction.sensorId + ",'" + prediction.date + "','" + prediction.time + "', " +
        prediction.predictedTemp + ");"

    executeQuery(cqlStatement)

    //    println("Prediction added")
  }

  def insertSensorModels(model: SensorModel): Unit = {
    checkSession

    val cqlStatement =
      "INSERT INTO " + keyspace + ".sensor_models(outputid, model, normalizer ) " +
        "VALUES(" + model.sensorId + ",'" + model.model + "','" + model.normalizer + "');"

    executeQuery(cqlStatement)

    //    println("Model added")
  }

  def insertSetTemperature(log: SetTemperatureLog): Unit = {
    checkSession()

    val cqlStatement =
      "INSERT INTO " + keyspace + ".set_temperatures(outputid, season, day, hour, quartile, time, settemperature) " +
        "VALUES(" + log.sensorId + ",'" + log.season + "','" + log.day + "'," +
        log.hour + "," + log.quartile + ", dateof(now()), " + log.setTemperature + ");"

    executeQuery(cqlStatement)
  }

  /*
   * Retrieve data
   */

  def getANNModelsForOutput(id: Int): Tuple2[MLRegression, NormalizationHelper] = {
    checkSession

    val cqlStatement = "SELECT * FROM " + keyspace + ".sensor_models;"
    val result = executeQuery(cqlStatement)

    if (result.isEmpty) {
      throw new RuntimeException("No results for output " + id)
    }

    val row = result.get.one()
    val hexStringModel = row.getString("model")
    val hexStringNormalizer = row.getString("normalizer")

    (EncogSerializer.deserializeModel(hexStringModel), EncogSerializer.deserializeNormalizationHelper(hexStringNormalizer))
  }

  def getMostRecentTemperatureEntries(outputId: Int, numberOfEntries: Int): Vector[SensorLog] = {
    checkSession

    var result: Vector[SensorLog] = Vector.empty

    val cqlStatement =
      "SELECT * FROM " + keyspace + ".sensor_logs " +
        "WHERE outputid = " + outputId + " " +
        "ORDER BY date DESC, time DESC " +
        "LIMIT " + numberOfEntries + ";"


    val queryResult = executeQuery(cqlStatement)

    if (queryResult.isEmpty) {
      throw new RuntimeException("No results for output " + outputId)
    }

    val resultSet = queryResult.get

    val resultIterator = resultSet.iterator()

    while (resultIterator.hasNext) {
      val row = resultIterator.next

      val id = row.getLong("outputid")
      val date = row.getString("date")
      val time = row.getString("time")
      val regime = row.getString("regime")
      val measuredTemp = row.getDouble("measuredtemperature")
      val setTemp = row.getDouble("settemperature")

      val log = new SensorLog(id.toInt, date, time, regime, measuredTemp, setTemp)

      result +:= log
    }

    result
  }

  def getDistinctUsers(): Vector[String] = {
    checkSession

    var result: Vector[String] = Vector.empty

    val cqlStatement =
      "SELECT DISTINCT user from " + keyspace + ".sensor_info;"

    val queryResult = executeQuery(cqlStatement)

    if (queryResult.isEmpty) {
      throw new RuntimeException("No users in sytem")
    }

    val resultSet = queryResult.get
    val resultIterator = resultSet.iterator()

    while (resultIterator.hasNext) {
      val row = resultIterator.next()
      result :+= row.getString("user")
    }

    result
  }

  def getSensorsForUser(user: String): Vector[SensorInfo] = {
    checkSession()

    var result: Vector[SensorInfo] = Vector.empty

    val cqlStatement =
      "SELECT * FROM " + keyspace + ".sensor_info " +
        "WHERE user = '" + user + "';"

    val queryResult = executeQuery(cqlStatement)

    if (queryResult.isEmpty) {
      throw new RuntimeException("No users in sytem")
    }

    val resultSet = queryResult.get
    val resultIterator = resultSet.iterator()

    while (resultIterator.hasNext) {
      val row = resultIterator.next()

      val user = row.getString("user")
      val outputId = row.getLong("outputid").toInt
      val location = row.getString("location")

      result :+= new SensorInfo(outputId, user, location)
    }
    result
  }

  def getAverageSetTempFor(sensorId: Int, season: String, day: String, hour: Int, quartile: Int): Double = {
    val cqlStatement =
      "SELECT settemperature FROM " + keyspace + ".set_temperatures " +
        "WHERE outputid = " + sensorId + " " +
        "and season = '" + season.toUpperCase + "' " +
        "and day = '" + day.toUpperCase + "' " +
        "and hour = " + hour + " " +
        "and quartile = " + quartile +
        ";"

    val queryResult = executeQuery(cqlStatement)

    if (queryResult.isEmpty) {
      throw new RuntimeException("No users in sytem")
    }

    val resultSet = queryResult.get
    val resultIterator = resultSet.iterator()
    var sum = 0.0
    var count = 0

    while (resultIterator.hasNext) {
      val row = resultIterator.next()

      val temp = row.getDouble("settemperature")
      sum += temp
      count += 1

    }
    sum / count
  }

  /*
   * Helpers
   */

  private def checkSession(): Unit = {
    if (session == null) {
      throw new RuntimeException("Cassandra session not initialized")
    }
  }
}
