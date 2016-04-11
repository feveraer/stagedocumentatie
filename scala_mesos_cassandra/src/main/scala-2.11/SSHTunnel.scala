import com.jcraft.jsch.{JSch, Session}

/**
  * Created by Lorenz on 11/04/2016.
  */
object SSHTunnel {
  private val host = "157.193.228.108"

  val lport = 9042
  val rhost = "10.11.12.110"
  val rport = 9042

  private var session: Session = null

  def connect(user: String, password: String): Unit = {
      // create ssh connection
      val jsch = new JSch()
      session = jsch.getSession(user, host, 22)
      session.setPassword(password)

      // Accept key file
      session.setConfig("StrictHostKeyChecking", "no")
      println("Establishing SSH Connection...")
      session.connect()

      // port forwarding van rhost naar localhost
      val assinged_port = session.setPortForwardingL("localhost" , lport, rhost, rport)
      println("localhost:" + assinged_port + " -> " + rhost + ":" + rport)
  }

  def disconnect(): Unit = {
    println("Disconnect SSH")
    session.disconnect
  }
}
