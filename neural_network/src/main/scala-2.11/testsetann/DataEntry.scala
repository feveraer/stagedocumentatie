package testsetann

/**
  * Created by Lorenz on 13/04/2016.
  */
case class DataEntry (time: Time, setTemp: Int, MeasuredTemp: Int) {
  def difference(time:Time): Unit ={
    this.time.difference(time)
  }
}
