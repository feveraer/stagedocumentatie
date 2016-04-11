import scala.io.StdIn

/**
  * Created by Lorenz on 11/04/2016.
  */
object CassandraMesos {
  private var user: String = null
  private var pw: String = null

  private var connected = false

  def main(args: Array[String]) {
    run()
  }

  def run() {
    connect()

    val results = CassandraConnection.executeQuery("SELECT * FROM twitter")

    if (!results.isEmpty) {

      val resultIterator = results.get.iterator()

      println("Read")
      println()
      while (resultIterator.hasNext()) {
        val row = resultIterator.next()
        printf("%s \t %s \t %s\n", row.getString("timestamp"), row.getString("username"), row.getString("tweet"))
      }
    }

    CassandraConnection.close()
    SSHTunnel.disconnect()
  }


  private def ask_username_and_pw(): Unit = {
    user = StdIn.readLine("Enter username: ")

    val console = System.console()
    if (console == null)
      pw = StdIn.readLine("Enter password: ")
    else
      console.readPassword("Enter password: ")
  }

  private def connect(): Unit = {
    ask_username_and_pw()

    while (!connected) {
      try {
        connectSSHandCassandra()
      }
      catch {
        case e: Exception =>
          println(e.getMessage)
          connect()
      }
    }
  }

  private def connectSSHandCassandra(): Unit = {
    SSHTunnel.connect(user, pw)
    CassandraConnection.connect()
    connected = true
  }
}
