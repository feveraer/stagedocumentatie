import cassandra.CassandraConnection
import ssh_tunnel.SSHTunnel

/**
  * Created by Lorenz on 25/04/2016.
  */
object TestCassandra {

  def main(args: Array[String]) {
    SSHTunnel.connect("root", "Ugent2015")

    CassandraConnection.connect
    CassandraConnection.close

    SSHTunnel.disconnect()
  }
}
