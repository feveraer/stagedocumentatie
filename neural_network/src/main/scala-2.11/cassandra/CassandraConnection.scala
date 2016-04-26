package cassandra

import com.datastax.driver.core.{Cluster, ResultSet, Session}
import connections.SSHTunnel


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

  def getSession(): Session = {
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

  def insertSensorLog(log: SensorLog): Unit = {
    if (session == null) {
      throw new RuntimeException("Cassandra session not initialized")
    }

    val cqlStatement =
      "INSERT INTO " + keyspace + ".sensor_logs (outputid, date, time, regime, measuredtemperature, settemperature) " +
        "VALUES(" + log.sensorId + ",'" + log.date + "','" + log.time + "','" + log.regime + "'," +
        log.measuredTemp + "," + log.setTemp + ");"

    executeQuery(cqlStatement)

//    println("Log inserted")
  }

  def insertSensorInfo(info: SensorInfo): Unit = {
    if (session == null) {
      throw new RuntimeException("Cassandra session not initialized")
    }

    val cqlStatement =
      "INSERT INTO " + keyspace + ".sensor_info (outputid, location, user) " +
        "VALUES(" + info.sensorId + ",'" + info.location + "','" + info.user + "')" +
        "IF NOT EXISTS;"

    executeQuery(cqlStatement)

//    println("Info inserted")
  }

  def insertSensorPrediction(prediction: SensorPrediction): Unit ={
    if (session == null) {
      throw new RuntimeException("Cassandra session not initialized")
    }

    val cqlStatement =
      "INSERT INTO " + keyspace + ".sensor_prediction(outputid, date, time, predictedTemp) " +
      "VALUES (" + prediction.sensorId + ",'" + prediction.date + "','" + prediction.time + "', " +
        prediction.predictedTemp +");"

    executeQuery(cqlStatement)

//    println("Prediction added")
  }

  // close connection with Cassandra cluster
  def close() {
    if (cluster != null) {
      cluster.close()
      println("Connection closed")
    }
  }
}
