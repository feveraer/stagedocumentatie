package testsetann

import java.util.{Calendar, Date, GregorianCalendar}

/**
  * Created by Lorenz on 13/04/2016.
  */

case class Time (year: Int, month: Int, day: Int, hour: Int, minutes: Int, seconds: Int) {

  def difference(time:Time): Time = {
//    var hourDiff=0
//    var minuteDiff=0
//
//    if(time.hour >= hour){
//      hourDiff = time.hour - hour
//    } else {
//      hourDiff = 24 - hour + time.hour
//    }
//    if(time.minutes >= minutes) {
//      minuteDiff = time.minutes - minutes
//    } else {
//      hourDiff -= 1
//      minuteDiff = 60 - (minutes - time.minutes)
//    }
//    if(hourDiff == -1) {
//      hourDiff = 23
//    }
//
//    new Time(hourDiff, minuteDiff)

    val d1 = new GregorianCalendar(year + 1900, month, day, hour, minutes, seconds)
    val d2 = new GregorianCalendar(time.year + 1900, time.month, time.day,
      time.hour, time.minutes, time.seconds)

    val diffInMillis = d2.getTimeInMillis - d1.getTimeInMillis
    // year, month, day, hour, minutes, seconds
    val diff = (

    )

    null
  }
}
