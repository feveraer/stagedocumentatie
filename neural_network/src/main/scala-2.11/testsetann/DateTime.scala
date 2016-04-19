package testsetann

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalTime}

/**
  * Created by Lorenz on 13/04/2016.
  */
case class DateTime(date: LocalDate, time: LocalTime) {

  def this(year: Int, month: Int, day: Int, hour: Int, minutes: Int, seconds: Int)
  = this(LocalDate.of(year, month, day), LocalTime.of(hour, minutes, seconds))

  def difference(time: DateTime): DateTimeDifference = {
    // Build dates and times of the Time objects
    val date1 = this.date
    val time1 = this.time

    val date2 = time.date
    val time2 = time.time

    // Calculate differences
    var dayDiff = date1.until(date2, ChronoUnit.DAYS).toInt
    var hourDiff = (time1.until(time2, ChronoUnit.HOURS) % 24).toInt
    var minuteDiff = (time1.until(time2, ChronoUnit.MINUTES) % 60).toInt
    var secondDiff = (time1.until(time2, ChronoUnit.SECONDS) % 60).toInt

    // make adjustments for negative differences
    if (secondDiff < 0) {
      secondDiff += 60
      minuteDiff -= 1
    }

    if (minuteDiff < 0) {
      minuteDiff += 60
      hourDiff -= 1
    }

    if (hourDiff < 0) {
      hourDiff += 24
    }

    // Adjustment in daydiff if the time component of the 2nd object is less than the timeComponent from the 1st object
    if (time2.isBefore(time1)) {
      dayDiff -= 1
    }

    val diff = new DateTimeDifference(
      dayDiff,
      hourDiff,
      minuteDiff,
      secondDiff
    )

    diff
  }
}

case class DateTimeDifference(days: Int, hours: Int, minutes: Int, seconds: Int) {
  def toMillis(): Long = {
    var long = 0
    long += (seconds * 1000)
    long += (minutes * 60 * 1000)
    long += (hours * 60 * 60 * 1000)
    long += (days * 24 * 60 * 60 * 1000)
    long
  }
}

