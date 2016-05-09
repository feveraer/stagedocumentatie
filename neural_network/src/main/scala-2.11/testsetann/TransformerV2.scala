package testsetann

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.{Files, Paths}
import java.time.LocalDateTime

import ann.Constants
import time.DateTime

import scala.io.{Source, StdIn}

/**
  * Created by Lorenz on 9/05/2016.
  */
object TransformerV2 {
  // Return Vector of data entries (time, setTemp, measured) from input tsv file.
  def getDataEntriesFromTSV(pathToTSV: String): Vector[DataEntryV2] = {
    val lines = Source.fromFile(pathToTSV).getLines()
    // skip header
    lines.next()

    var vector: Vector[DataEntryV2] = Vector()

    while (lines.hasNext) {
      val line = lines.next()
      val parts = line.split("\t")
      val time = LocalDateTime.of(parts(0).toInt, parts(1).toInt,
        parts(2).toInt, parts(3).toInt, parts(4).toInt, parts(5).toInt)
      val setTemp = parts(9).toInt
      val measured = parts(10).toInt
      vector = vector :+ new DataEntryV2(time, setTemp, measured)
    }
    vector
  }

  // Write data entries to tsv file to be used as training data for ANN.
  // TSV structure: SetTemp - MeasuredTemp - HourDiff - MinuteDiff - NextMeasured
  def writeDataEntriesToTSV(vector: Vector[DataEntryV2]): Unit = {
    var file: File = null
    if (Files.exists(Paths.get(Constants.RESOURCES_PATH + Constants.DEFAULT_TRAINING_SET_PATH))) {
      val fileName = StdIn.readLine(
        Constants.DEFAULT_TRAINING_SET_PATH + " already exists. Enter new file name: ")
      file = new File(Constants.RESOURCES_PATH + fileName)
    } else {
      file = new File(Constants.RESOURCES_PATH + Constants.DEFAULT_TRAINING_SET_PATH)
    }
    val bw = new BufferedWriter(new FileWriter(file, true))

    // header
    bw.write("SetTemp\tMeasuredTemp\tTimeDiff\tNextMeasured\n")

    // Loop over all data entries but the last one since the NextMeasured column of
    // every entry predicts the next entry in line.
    for (i <- 0 until (vector.size - 1)) {
      // data entry
      val de = vector(i)
      // time difference
      val td = de.difference(vector(i + 1))

      bw.write(de.setTemp + "\t" + de.measuredTemp + "\t" + td + "\t"
        + vector(i + 1).measuredTemp + "\n")
      bw.flush()
    }
  }
}
