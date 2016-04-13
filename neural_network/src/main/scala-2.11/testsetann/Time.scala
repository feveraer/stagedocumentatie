package testsetann

/**
  * Created by Lorenz on 13/04/2016.
  */
case class Time (hour: Int, minute: Int) {

  def difference(time:Time): Time = {
    var hourDiff=0
    var minuteDiff=0

    if(time.hour >= hour){
      hourDiff = time.hour - hour
    } else {
      hourDiff = 24 - hour + time.hour
    }
    if(time.minute >= minute) {
      minuteDiff = time.minute - minute
    } else {
      hourDiff -= 1
      minuteDiff = 60 - (minute - time.minute)
    }
    if(hourDiff == -1) {
      hourDiff = 23
    }

    new Time(hourDiff, minuteDiff)
  }
}
