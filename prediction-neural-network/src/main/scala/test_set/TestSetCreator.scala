package test_set

import java.io.{BufferedWriter, File, FileWriter}

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Lorenz on 5/04/2016.
  */
class TestSetCreator {

  val conf = new SparkConf().setAppName("Simple Application").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  val measuredTempsDF = createDF()

  def createDF(): DataFrame = {
    // BUG FIX WINDOWS: spark needs to be able to locate winutils.exe
    // http://qnalist.com/questions/4994960/run-spark-unit-test-on-windows-7
    // System.setProperty("hadoop.home.dir", "d:\\winutil\\")

    println("Create DF's")

    // Turn of logging
    LogManager.getRootLogger.setLevel(Level.OFF)

    val qbusReader = new QbusReader(sqlContext)

    val outputLogsDF = qbusReader.read(QbusReader.outputLogs, QbusReader.outputLogsSchema)
    val outputGraphHourDataDF = qbusReader.read(QbusReader.outputGraphHourData, QbusReader.outputGraphHourDataSchema)
    val outputsDF = qbusReader.read(QbusReader.outputs, QbusReader.outputsSchema)
    val locationsDF = qbusReader.read(QbusReader.locations, QbusReader.locationsSchema)
    val typesDF = qbusReader.read(QbusReader.types, QbusReader.typesSchema)

    outputLogsDF.registerTempTable("OutputLogs")
    outputGraphHourDataDF.registerTempTable("OutputGraphHourData")
    outputsDF.registerTempTable("Outputs")
    locationsDF.registerTempTable("Locations")
    typesDF.registerTempTable("Types")

    sqlContext.sql("select oghd.Time, oghd.Value, l.Userid, l.Name as LocationName "
      + "from OutputGraphHourData oghd "
      + "join Outputs o on oghd.OutputID = o.Id "
      + "join Locations l on o.LocationId = l.Id "
      + "join Types t on o.TypeId = t.Id "
      + "where t.Name = 'THERMO' and oghd.Time > '2016-02-22'"
    )
  }

  def createTestSets(): Unit = {
    println("Get distinct users")
    measuredTempsDF.registerTempTable("measuredTempsDF")
    val distinctUsersDF = sqlContext.sql("select distinct Userid from measuredTempsDF").rdd
    val distinctUsersRow = distinctUsersDF.collect()
    val distinctUsers = distinctUsersRow.map(x => x.getString(0))

    val fileValuesSet = new File("src/valuesSet.txt")
    val bwValuesSet = new BufferedWriter(new FileWriter(fileValuesSet))

    val fileExpectedSet = new File("src/ExpectedSet.txt")
    val bwExpectedSet = new BufferedWriter(new FileWriter(fileExpectedSet))

    distinctUsers.foreach(x => {
      val user = x
      println("Get distinct locations for user: " + user)
      val distinctLocationsDF = sqlContext.sql("select distinct LocationName from measuredTempsDF where Userid = " + user).rdd
      val distinctLocationsRow = distinctLocationsDF.collect()
      val distinctLocations = distinctLocationsRow.map(x => x.getString(0))

      distinctLocations.foreach(y => {
        val location = y
        val valuesDF = sqlContext.sql("select Value from measuredTempsDF where Userid = " + user + " and LocationName = '" + location + "'").rdd
        val valuesRow = valuesDF.collect()
        val values = valuesRow.map(x => x.getString(0))

        println(user + " " + location + " " + values.size)

        for (i <- 0 to (values.size - 49)){
          val series = values.slice(i, i+48)
          println(series.toString)
          bwValuesSet.write(series.toString + "\n")
          val expected = values(i+48)
          println(expected.toString)
          bwExpectedSet.write(expected.toString + "\n")
        }

      })
    })

    bwValuesSet.close
    bwExpectedSet.close

  }
}
