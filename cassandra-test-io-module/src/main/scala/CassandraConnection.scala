import com.datastax.driver.core.{Cluster, ResultSet, Session}

/**
  * Created by Lorenz on 31.03.16.
  */
class CassandraConnection {
  // config variables
  val ip_address = "192.168.22.101"
  val port = 9042
  val keyspace = "testdata"

  var cluster: Cluster = null
  var session: Session = null

  // create conection to cluster
  def connect(): Session = {
    cluster = Cluster.builder().addContactPoint(ip_address).withPort(port).build()
    val metadata = cluster.getMetadata()
    printf("Connected to cluster: %s\n", metadata.getClusterName())

    session = cluster.connect(keyspace);
    session;
  }

  def getSession() : Session = {
    if (session != null){
      session
    } else {
      connect()
      session
    }
  }

  def executeQuery(cqlStatement: String): ResultSet = {
    session.execute(cqlStatement)
  }

  // close connection with Cassandra cluster
  def close() {
    if(cluster != null) {
      cluster.close()
      println("Connection closed")
    }
  }
}
