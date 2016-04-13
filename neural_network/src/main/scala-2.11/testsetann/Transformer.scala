package testsetann

import java.io.{BufferedWriter, File, FileWriter}

import scala.io.Source

/**
  * Created by Lorenz on 13/04/2016.
  */
object Transformer {
  def tsvToDataEntries(pathToTSV: String): Vector[DataEntry] = {
    val lines = Source.fromFile(pathToTSV).getLines()
    // skip header
    lines.next()

    var vector: Vector[DataEntry] = Vector()

    while(lines.hasNext){
      val line = lines.next()
      val parts = line.split("\t")
      val time = new Time(parts(3).toInt,parts(4).toInt)
      val setTemp = parts(9).toInt
      val measured = parts(10).toInt
      vector = vector :+ new DataEntry(time,setTemp, measured)
    }
    vector
  }

  def DataEntryVectorToTSV(vector: Vector[DataEntry]): Unit = {
    val file = new File("src/main/resources/TrainingsSet.tsv")
    val bw = new BufferedWriter(new FileWriter(file, true))

    bw.write("SetTemp \t  MeasuredTemp \t hourDiff \t minuteDiff \t Output \n")

    for (i <- 0 until (vector.size - 1)){
      val de = vector(i)
      val td = de.difference(vector(i+1))

      bw.write(de.setTemp + "\t" + de.measuredTemp + "\t" + td.hour + "\t" + td.minute + "\t" + vector(i+1).measuredTemp + "\n")
      bw.flush()
    }
  }
}
