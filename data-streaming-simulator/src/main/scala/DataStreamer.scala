import org.apache.spark.sql.DataFrame

/**
  * Created by Lorenz on 6/04/2016.
  */
object DataStreamer {
  val measuredTempsDataFrame = DataImporter.measuredTempsData()
  measuredTempsDataFrame.registerTempTable("measuredTempsDataFrame")

  def createOrderedTempDataDF(): DataFrame ={
    measuredTempsDataFrame.orderBy("Time")
  }

  def spewData(): Unit = {
    println("Collecting ordered data")
    val orderedTempDataRows = createOrderedTempDataDF().collect()

    var prevTimestamp = orderedTempDataRows(0).get(0)

    orderedTempDataRows.foreach(row => {
      val time = row.get(0)
      val value = row.getString(1)
      val userId = row.getString(2)
      val location = row.getString(3)

      if (prevTimestamp != time){
        Thread.sleep(5000)
      }

      println(time + "\t" + value + "\t" + userId + "\t" + location)
      prevTimestamp = time
    })

  }

  def main(args: Array[String]) {
    spewData()
  }

}
