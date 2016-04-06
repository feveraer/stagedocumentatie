package test_set

import java.io.{BufferedWriter, File, FileWriter, PrintWriter}

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

  val measuredTempsDF = createMeasuredTempDF()

  val sizeOfSampleForPrediction = 48

  /**
    *
    * @return returns the measuredTemps DataFrame
    */
  def createMeasuredTempDF(): DataFrame = {
    // BUG FIX WINDOWS: spark needs to be able to locate winutils.exe
    // http://qnalist.com/questions/4994960/run-spark-unit-test-on-windows-7
    // System.setProperty("hadoop.home.dir", "d:\\winutil\\")
    println("Create DF's")

    // Turn of logging
    LogManager.getRootLogger.setLevel(Level.OFF)

    // Use qbus reader to create the dataframes
    val qbusReader = new QbusReader(sqlContext)

    val outputLogsDF = qbusReader.read(QbusReader.outputLogs, QbusReader.outputLogsSchema)
    val outputGraphHourDataDF = qbusReader.read(QbusReader.outputGraphHourData, QbusReader.outputGraphHourDataSchema)
    val outputsDF = qbusReader.read(QbusReader.outputs, QbusReader.outputsSchema)
    val locationsDF = qbusReader.read(QbusReader.locations, QbusReader.locationsSchema)
    val typesDF = qbusReader.read(QbusReader.types, QbusReader.typesSchema)

    // register de dataframes as Tables
    outputLogsDF.registerTempTable("OutputLogs")
    outputGraphHourDataDF.registerTempTable("OutputGraphHourData")
    outputsDF.registerTempTable("Outputs")
    locationsDF.registerTempTable("Locations")
    typesDF.registerTempTable("Types")

    // Make the measuredTemps DataFrame
    sqlContext.sql("select oghd.Time, oghd.Value, l.Userid, l.Name as LocationName "
      + "from OutputGraphHourData oghd "
      + "join Outputs o on oghd.OutputID = o.Id "
      + "join Locations l on o.LocationId = l.Id "
      + "join Types t on o.TypeId = t.Id "
      + "where t.Name = 'THERMO'"
    )
  }

  /**
    * This method creates the files to train the ANN with
    */
  def createTestSets(): Unit = {
    // Get all distinct users
    println("Get distinct users")
    measuredTempsDF.registerTempTable("measuredTempsDF")
    val distinctUsersDF = sqlContext.sql("select distinct Userid from measuredTempsDF").rdd
    val distinctUsersRow = distinctUsersDF.collect()
    val distinctUsers = distinctUsersRow.map(x => x.getString(0))

    // create the outputfiles en de writers here so they can be accessed in the foreach loops below
    val fileValuesSet = new File("src/SampleSet.txt")
    val bwValuesSet = new BufferedWriter(new FileWriter(fileValuesSet, true))

    val fileExpectedSet = new File("src/ExpectedSet.txt")
    val bwExpectedSet = new BufferedWriter(new FileWriter(fileExpectedSet, true))

    // Loop through all users
    distinctUsers.foreach(x => {
      val user = x
      // get all distinct locations for this user
      println("Get distinct locations for user: " + user)
      val distinctLocationsDF = sqlContext.sql("select distinct LocationName from measuredTempsDF where Userid = " + user).rdd
      val distinctLocationsRow = distinctLocationsDF.collect()
      val distinctLocations = distinctLocationsRow.map(x => x.getString(0))

      // Loop through all locations for the user
      distinctLocations.foreach(y => {

        // Get all the values for the user on the given location
        val location = y
        val valuesDF = sqlContext.sql("select Value from measuredTempsDF where Userid = " + user + " and LocationName = '" + location + "'").rdd
        val valuesRow = valuesDF.collect()
        val values = valuesRow.map(x => x.getString(0))

        println(user + " " + location + " " + values.length)

        // Write the trainingsdata
        for (i <- 0 to (values.length - sizeOfSampleForPrediction + 1)) {
          val series = values.slice(i, i + sizeOfSampleForPrediction)

          series.foreach(value => {
            bwValuesSet.write(value + ",")
          })
          bwValuesSet.write("\n")

          val expected = values(i + sizeOfSampleForPrediction)
          bwExpectedSet.write(expected.toString + "\n")

        }

        // flush buffers to files
        bwValuesSet.flush()
        bwExpectedSet.flush()

      })
    })

    // close writers
    bwValuesSet.close()
    bwExpectedSet.close()

  }
}
