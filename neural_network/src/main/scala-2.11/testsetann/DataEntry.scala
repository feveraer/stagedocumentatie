package testsetann

/**
  * Created by Lorenz on 13/04/2016.
  */
case class DataEntry (time: Time, setTemp: Int, measuredTemp: Int) {
  def difference(entry:DataEntry): Time ={
    this.time.difference(entry.time)
  }
}
