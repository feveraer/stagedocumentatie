package testsetann

/**
  * Created by Lorenz on 13/04/2016.
  */
case class DataEntry (time: DateTime, setTemp: Int, measuredTemp: Int) {
  def difference(entry:DataEntry): DateTimeDifference ={
    this.time.difference(entry.time)
  }
}
