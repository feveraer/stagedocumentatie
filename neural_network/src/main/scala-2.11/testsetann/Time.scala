package testsetann

import java.util.{Calendar, Date, GregorianCalendar}

/**
  * Created by Lorenz on 13/04/2016.
  */

case class Time (year: Int, month: Int, day: Int, hour: Int, minutes: Int, seconds: Int) {

  def difference(time:Time): Time = {
    val d1 = new GregorianCalendar(year + 1900, month, day, hour, minutes, seconds)
    val d2 = new GregorianCalendar(time.year + 1900, time.month, time.day,
      time.hour, time.minutes, time.seconds)

    val diffInMillis = d2.getTimeInMillis - d1.getTimeInMillis
    val diffAsDate = new GregorianCalendar()
    diffAsDate.setTimeInMillis(diffInMillis)

    val diff = Time(
      diffAsDate.get(Calendar.YEAR),
      diffAsDate.get(Calendar.MONTH),
      diffAsDate.get(Calendar.DAY_OF_MONTH),
      diffAsDate.get(Calendar.HOUR),
      diffAsDate.get(Calendar.MINUTE),
      diffAsDate.get(Calendar.SECOND)
    )
    diff
  }
}
