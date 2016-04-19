package testsetann

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalTime}

/**
  * Created by Lorenz on 13/04/2016.
  */
object DateTime {
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

case class DateTime(date: LocalDate, time: LocalTime) {

  def this(year: Int, month: Int, day: Int, hour: Int, minutes: Int, seconds: Int)
  = this(LocalDate.of(year, month, day),LocalTime.of(hour, minutes, seconds))

  def difference(time: DateTime): DateTimeDifference = {
    // Build dates and times of the Time objects
    val date1 = this.date
    val time1 = this.time

    val date2 = time.date
    val time2 = time.time

    // Calculate differences
    val yearDiff = date1.until(date2, ChronoUnit.YEARS).toInt
    val monthDiff = (date1.until(date2, ChronoUnit.MONTHS) % 12).toInt
    var dayDiff = date1.until(date2, ChronoUnit.DAYS).toInt
    var hourDiff = (time1.until(time2, ChronoUnit.HOURS) % 24).toInt
    var minuteDiff = (time1.until(time2, ChronoUnit.MINUTES) % 60).toInt
    var secondDiff = (time1.until(time2, ChronoUnit.SECONDS) % 60).toInt

    // make adjustments for negative differences
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
      dayDiff = DateTime.DAYS_IN_MONTH(date1.getYear, date1.getMonthValue) + dayDiff
    }

    // Adjustment in daydiff if the time component of the 2nd object is less than the timeComponent from the 1st object
    if (time2.isBefore(time1)) {
      dayDiff = dayDiff - 1
    }

    // reduce the day diff to a value between 0 and 31
    if (monthDiff > 0 || yearDiff > 0) {
      // Create a date iterator
      // Choose the year and month from the 1st object and the day of the 2nd one
      var dateIter = LocalDate.of(date1.getYear, date1.getMonth, date2.getDayOfMonth)
      var sum = 0

      // Iterate over all the months between the first and second date
      // and count the total number of days that have passed
      while (dateIter.isBefore(date2)) {
        val year = dateIter.getYear
        val month = dateIter.getMonthValue
        sum = sum + DateTime.DAYS_IN_MONTH(year, month)
        dateIter = dateIter.plus(1, ChronoUnit.MONTHS)
      }

      // Adjust the day diff
      dayDiff = dayDiff - sum
      if(dayDiff < 0){
        dayDiff = DateTime.DAYS_IN_MONTH(date1.getYear, date1.getMonthValue) + dayDiff + 1
      }
    }

    val diff = new DateTimeDifference(
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

case class DateTimeDifference(year: Int, month: Int, day: Int, hour: Int, minutes: Int, seconds: Int)

