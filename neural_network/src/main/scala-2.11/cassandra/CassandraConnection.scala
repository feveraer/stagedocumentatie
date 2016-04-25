package cassandra

import com.datastax.driver.core.{Cluster, ResultSet, Session}
import ssh_tunnel.SSHTunnel


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
    val metadata = cluster.getMetadata()
    printf("Connected to cluster: %s\n", metadata.getClusterName())

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

  def insertSensorLog(sensorLog: SensorLog): Unit = {
    if(session == null){
      throw new RuntimeException("Cassandra session not initialized")
    }
  }

  def insertSensorInfo(sensorInfo: SensorInfo): Unit ={

  }

  // close connection with Cassandra cluster
  def close() {
    if (cluster != null) {
      cluster.close()
      println("Connection closed")
    }
  }
}
