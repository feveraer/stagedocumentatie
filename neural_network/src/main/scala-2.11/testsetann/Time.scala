package testsetann

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalTime}

/**
  * Created by Lorenz on 13/04/2016.
  */
object Time {
  private val DAYS_IN_MONTH_NORMAL_YEAR = Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
  private val DAYS_IN_MONTH_LEAP_YEAR = Array(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

  def DAYS_IN_MONTH(year: Int, month: Int): Int = {
    if (IS_LEAP_YEAR(year)) {
      return DAYS_IN_MONTH_LEAP_YEAR(month - 1)
    }
    DAYS_IN_MONTH_NORMAL_YEAR(month - 1)
  }

  def IS_LEAP_YEAR(year: Int): Boolean = {
    val date = LocalDate.of(year, 2, 28)
    date.isLeapYear
  }
}

case class Time(year: Int, month: Int, day: Int, hour: Int, minutes: Int, seconds: Int) {

  def difference(time: Time): Time = {
    // Bouw datum en tijd
    val date1 = LocalDate.of(year, month, day)
    val time1 = LocalTime.of(hour, minutes, seconds)

    val date2 = LocalDate.of(time.year, time.month, time.day)
    val time2 = LocalTime.of(time.hour, time.minutes, time.seconds)

    // verschillen berekenen
    val yearDiff = date1.until(date2, ChronoUnit.YEARS).toInt
    val monthDiff = (date1.until(date2, ChronoUnit.MONTHS) % 12).toInt
    var dayDiff = date1.until(date2, ChronoUnit.DAYS).toInt
    var hourDiff = (time1.until(time2, ChronoUnit.HOURS) % 24).toInt
    var minuteDiff = (time1.until(time2, ChronoUnit.MINUTES) % 60).toInt
    var secondDiff = (time1.until(time2, ChronoUnit.SECONDS) % 60).toInt

    if (secondDiff < 0) {
      secondDiff = 60 + secondDiff
      minuteDiff = minuteDiff - 1
    }

    if (minuteDiff < 0) {
      minuteDiff = 60 + minuteDiff
      hourDiff = hourDiff - 1
    }

    if (hourDiff < 0) {
      hourDiff = 24 + hourDiff
    }

    if (dayDiff < 0) {
      dayDiff = Time.DAYS_IN_MONTH(year, month) + dayDiff
    }

    if (time2.isBefore(time1)) {
      dayDiff = dayDiff - 1
    }

    if (monthDiff > 0 || yearDiff > 0) {
      var dateIter = LocalDate.of(date1.getYear, date1.getMonth, date2.getDayOfMonth)
      var sum = 0

      while (dateIter.isBefore(date2)) {
        val year = dateIter.getYear
        val month = dateIter.getMonthValue
        sum = sum + Time.DAYS_IN_MONTH(year, month)
        dateIter = dateIter.plus(1, ChronoUnit.MONTHS)
      }

      dayDiff = dayDiff - sum

      if(dayDiff < 0){
        dayDiff = Time.DAYS_IN_MONTH(date1.getYear, date1.getMonthValue) + dayDiff + 1
      }
    }

    val diff = new Time(
      yearDiff,
      monthDiff,
      dayDiff,
      hourDiff,
      minuteDiff,
      secondDiff
    )

    diff
  }
}
