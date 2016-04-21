import com.jcraft.jsch.{JSch, Session}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Frederic on 20/04/2016.
  */
class SSHTunnelTest extends FlatSpec with Matchers {

  private val jsch = new JSch()
  private val user = "root"
  private val host = "cloudera.ugent.be"
  private val rhost = "cl06.ugent.be"
  private val pw = "Ugent2012"
  private var session: Session = null

  "SSH to " + user + "@" + host should "establish a connection successfully" in {
    session = jsch.getSession(user, host, 22)
    session.setPassword(pw)
    // Accept key file
    session.setConfig("StrictHostKeyChecking", "no")
    println("Establishing SSH Connection...")
    session.connect()
    session.isConnected should be (true)
  }

  "SSH tunnel to " + user + "@" + rhost should "establish a connection successfully" in {

  }
}
