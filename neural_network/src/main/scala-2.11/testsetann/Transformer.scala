package testsetann

import java.io.{BufferedWriter, File, FileWriter}

import scala.io.Source

/**
  * Created by Lorenz on 13/04/2016.
  */
object Transformer {

  // Return Vector of data entries (time, setTemp, measured) from input tsv file.
  def getDataEntriesFromTSV(pathToTSV: String): Vector[DataEntry] = {
    val lines = Source.fromFile(pathToTSV).getLines()
    // skip header
    lines.next()

    var vector: Vector[DataEntry] = Vector()

    while(lines.hasNext){
      val line = lines.next()
      val parts = line.split("\t")
      val time = new DateTime(parts(0).toInt, parts(1).toInt,
        parts(2).toInt, parts(3).toInt,parts(4).toInt, parts(5).toInt)
      val setTemp = parts(9).toInt
      val measured = parts(10).toInt
      vector = vector :+ new DataEntry(time,setTemp, measured)
    }
    vector
  }

  // Write data entries to tsv file to be used as training data for ANN.
  // TSV structure: SetTemp - MeasuredTemp - HourDiff - MinuteDiff - NextMeasured
  def writeDataEntriesToTSV(vector: Vector[DataEntry]): Unit = {
    val file = new File("src/main/resources/TrainingsSet.tsv")
    val bw = new BufferedWriter(new FileWriter(file, true))

    // header
    bw.write("SetTemp\tMeasuredTemp\tHourDiff\tMinuteDiff\tNextMeasured\n")

    // Loop over all data entries but the last one since the NextMeasured column of
    // every entry predicts the next entry in line.
    for (i <- 0 until (vector.size - 1)){
      // data entry
      val de = vector(i)
      // time difference
      val td = de.difference(vector(i+1))

      bw.write(de.setTemp + "\t" + de.measuredTemp + "\t" + td.hour + "\t" + td.minutes + "\t" + vector(i+1).measuredTemp + "\n")
      bw.flush()
    }
  }
}
