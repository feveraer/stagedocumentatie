
package simulator.cassandraTestPackage

import cassandra.{EncogSerializer, _}
import com.datastax.driver.core.{Cluster, ResultSet, Session}
import connections.SSHTunnel
import helpers.Helpers.{Quartile, Season}
import org.apache.log4j.Logger
import org.encog.ml.MLRegression
import org.encog.ml.data.versatile.NormalizationHelper
import time.DateTime

/**
  * Created by Lorenz on 4/05/2016.
  */
object CassandraTestConnection {
  private val logger = Logger.getLogger("CassandraConnection")
  // config variables
  val keyspace = "qbustest"

  var cluster: Cluster = null
  var session: Session = null

  // create connection to cluster
  def connect(): Session = {
    cluster = Cluster.builder().addContactPoint("localhost").withPort(SSHTunnel.lportCassandra).build()
    val metadata = cluster.getMetadata
    logger.info("Connected to cluster: " + metadata.getClusterName + "\n")

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
        logger.warn(e.getMessage)
        None
    }
  }

  // close connection with Cassandra cluster
  def close() {
    if (cluster != null) {
      cluster.close()
      logger.info("Connection closed")
    }
  }

  /*
   * INSERTS
   */

  def insertSensorLog(log: SensorLog): Unit = {
    checkSession()

    val cqlStatement =
      "INSERT INTO " + keyspace + ".sensor_logs (outputid, date, time, regime, measuredtemperature, settemperature) " +
        "VALUES(" + log.sensorId + ",'" + log.date + "','" + log.time + "','" + log.regime + "'," +
        log.measuredTemp + "," + log.setTemp + ");"

    executeQuery(cqlStatement)

    logger.debug("Log inserted")
  }

  def insertHistoricSensorLog(log: SensorLog): Unit = {
    checkSession()

    val cqlStatement =
      "INSERT INTO " + keyspace + ".historic_sensor_logs (outputid, date, time, regime, measuredtemperature, settemperature) " +
        "VALUES(" + log.sensorId + ",'" + log.date + "','" + log.time + "','" + log.regime + "'," +
        log.measuredTemp + "," + log.setTemp + ");"

    executeQuery(cqlStatement)

    logger.debug("Log inserted")
  }

  def insertSensorModels(model: SensorModel): Unit = {
    checkSession()

    val cqlStatement =
      "INSERT INTO " + keyspace + ".sensor_models(outputid, model, normalizer ) " +
        "VALUES(" + model.sensorId + ",'" + model.model + "','" + model.normalizer + "');"

    executeQuery(cqlStatement)

    logger.debug("Model added")
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

  def getANNModelsForOutput(id: Int): (NormalizationHelper, MLRegression) = {
    checkSession()

    val cqlStatement = "SELECT * FROM " + keyspace + ".sensor_models;"
    val result = executeQuery(cqlStatement)

    if (result.isEmpty) {
      throw new RuntimeException("No results for output " + id)
    }

    val row = result.get.one()
    val hexStringModel = row.getString("model")
    val hexStringNormalizer = row.getString("normalizer")

    (EncogSerializer.deserializeNormalizationHelper(hexStringNormalizer), EncogSerializer.deserializeModel(hexStringModel))
  }

  def getMostRecentTemperatureEntries(outputId: Int, numberOfEntries: Int): Vector[SensorLog] = {
    checkSession()

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

  def getAverageSetTempFor(sensorId: Int, season: String, day: String, hour: Int, quartile: Int): Double = {
    checkSession()

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
      throw new RuntimeException("Nothing to calculate average from")
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

  // Returns average temperature for specified settings
  // If average is not available then return most recent set temp
  // Convenience method
  def getSetTempFor(log: SensorLog): Double = {
    checkSession()

    val dateTime = new DateTime(log.date, log.time)
    val date = dateTime.date
    val time = dateTime.time

    getSetTempFor(log.sensorId, Season.getSeason(date), date.getDayOfWeek.toString, time.getHour, Quartile.getQuartile(time))
  }

  // Returns average temperature for specified settings
  // If average is not available then return most recent set temp
  def getSetTempFor(sensorId: Int, season: String, day: String, hour: Int, quartile: Int): Double = {
    checkSession()

    try {
      val average = getAverageSetTempFor(sensorId, season, day, hour, quartile)
      average
    } catch {
      case e: RuntimeException =>
        val mostRecentLog = getMostRecentTemperatureEntries(sensorId, 1)
        mostRecentLog(0).setTemp
    }
  }

  def getAllLogsForSensor(outputId: Int): Vector[SensorLog] = {
    checkSession()

    var result: Vector[SensorLog] = Vector.empty

    val cqlStatement =
      "SELECT * FROM " + keyspace + ".sensor_logs " +
        "WHERE outputid = " + outputId + " " +
        "ORDER BY date DESC, time DESC " + ";"


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

  /*
   * Helpers
   */
  private def checkSession(): Unit = {
    if (session == null) {
      throw new RuntimeException("CassandraTestConnection session not initialized")
    }
  }
}
