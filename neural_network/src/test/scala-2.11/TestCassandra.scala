import java.io.{File, FileInputStream, ObjectInputStream}

import ann.{Constants}
import cassandra.{CassandraConnection, SensorModel}
import connections.SSHTunnel
import org.encog.ml.MLRegression
import org.encog.ml.data.versatile.NormalizationHelper
import org.encog.persist.EncogDirectoryPersistence

/**
  * Created by Lorenz on 25/04/2016.
  */
object TestCassandra {

  def main(args: Array[String]) {
    SSHTunnel.connect("root", "Ugent2015")

    CassandraConnection.connect

    testModelExport

    CassandraConnection.close

    SSHTunnel.disconnect()
  }

  def testModelExport: Unit = {
    val pathToNormalizationHelper = Constants.RESOURCES_PATH + Constants.ENCOG_NORMALIZATION_HELPER_PATH
    val pathToModel = Constants.RESOURCES_PATH + Constants.ENCOG_BEST_METHOD_PATH

    try {
      val fin: FileInputStream = new FileInputStream(pathToNormalizationHelper)
      val ois: ObjectInputStream = new ObjectInputStream(fin)
      val helper = ois.readObject.asInstanceOf[NormalizationHelper]
      ois.close

      val model = EncogDirectoryPersistence.loadObject(new File(pathToModel)).asInstanceOf[MLRegression]

      val entry = SensorModel.build(0, model, helper)

      CassandraConnection.insertSensorModels(entry)

      val result = CassandraConnection.getANNModelsForOutput(17)
      val model_result = result._1
      val normalizer_result = result._2

      val resultQueryRecent = CassandraConnection.getMostRecentTemperatureEntries(206252,3)

      println(resultQueryRecent)
    }
    catch {
      case e: Any => {
        e.printStackTrace
      }
    }
  }
}
