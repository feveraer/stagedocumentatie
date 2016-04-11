import scala.io.StdIn

/**
  * Created by Lorenz on 11/04/2016.
  */
object App {
  def main(args: Array[String]) {
    val user = StdIn.readLine("Enter username: ")
    var pw:String = null
    val console = System.console()
    if (console == null)
      pw = StdIn.readLine("Enter password: ")
    else
      console.readPassword("Enter password: ")

    SSHTunnel.connect(user, pw)
    CassandraConnection.connect()

    val results = CassandraConnection.executeQuery("SELECT * FROM twitter")
    val resultIterator = results.iterator()

    println("Read")
    println()
    while(resultIterator.hasNext()){
      val row = resultIterator.next()
      printf("%s \t %s \t %s\n", row.getString("timestamp"), row.getString("username"), row.getString("tweet"))
    }

    CassandraConnection.close()
    SSHTunnel.disconnect()
  }
}
