package cassandra

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import com.datastax.driver.core.utils.Bytes
import org.encog.ml.MLRegression
import org.encog.ml.data.versatile.NormalizationHelper

/**
  * Created by Lorenz on 26/04/2016.
  */
object EncogSerializer {
  def serialize(obj: Object): String  = {
    val b = new ByteArrayOutputStream()
    val o = new ObjectOutputStream(b)
    o.writeObject(obj)
    Bytes.toHexString(b.toByteArray)
  }

  def deserialize(hexString: String): Object = {
    val ex = Bytes.fromHexString(hexString);
    val ois = new ObjectInputStream(new ByteArrayInputStream(ex.array()))
    ois.readObject()
  }

  def deserializeNormalizationHelper(hexString: String): NormalizationHelper = {
    deserialize(hexString).asInstanceOf[NormalizationHelper]
  }

  def deserializeModel(hexString: String): MLRegression = {
    deserialize(hexString).asInstanceOf[MLRegression]
  }
}
