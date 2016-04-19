package testsetann

import java.util.{Calendar, GregorianCalendar}

/**
  * Created by Lorenz on 13/04/2016.
  */
object Time {
  val MILLIS_IN_SECOND: Long = 1000
  val MILLIS_IN_MINUTE: Long = MILLIS_IN_SECOND * 60
  val MILLIS_IN_HOUR: Long = MILLIS_IN_MINUTE * 60
  val MILLIS_IN_DAY: Long = MILLIS_IN_HOUR * 24
  val MILLIS_IN_31_DAYS_MONTH: Long = MILLIS_IN_DAY * 31
  val MILLIS_IN_30_DAYS_MONTH: Long = MILLIS_IN_DAY * 30
  val MILLIS_IN_29_DAYS_MONTH: Long = MILLIS_IN_DAY * 29
  val MILLIS_IN_28_DAYS_MONTH: Long = MILLIS_IN_DAY * 28
  val MILLIS_IN_NORMAL_YEAR: Long = 7 * MILLIS_IN_31_DAYS_MONTH + 4 * MILLIS_IN_30_DAYS_MONTH + MILLIS_IN_28_DAYS_MONTH
  val MILLIS_IN_LEAP_YEAR: Long = 7 * MILLIS_IN_31_DAYS_MONTH + 4 * MILLIS_IN_30_DAYS_MONTH + MILLIS_IN_29_DAYS_MONTH

  private val DAYS_IN_MONTH_NORMAL_YEAR = Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
  private val DAYS_IN_MONTH_LEAP_YEAR = Array(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

  def DAYS_IN_MONTH(year: Int, month: Int): Int = {
    if (IS_LEAP_YEAR(year)) {
      DAYS_IN_MONTH_LEAP_YEAR(month-1)
    }
    DAYS_IN_MONTH_NORMAL_YEAR(month-1)
  }

  def IS_LEAP_YEAR(year: Int): Boolean = {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365
  }
}

case class Time(year: Int, month: Int, day: Int, hour: Int, minutes: Int, seconds: Int) {

  def difference(time: Time): Time = {
    val d1 = new GregorianCalendar(year + 1900, month, day, hour, minutes, seconds)
    val d2 = new GregorianCalendar(time.year + 1900, time.month, time.day,
      time.hour, time.minutes, time.seconds)

    val diffInMillis = d2.getTimeInMillis - d1.getTimeInMillis

    var yearDiff = 0
    var monthDiff = 0
    val dayDiff = (diffInMillis / Time.MILLIS_IN_DAY % Time.DAYS_IN_MONTH(year, month)).toInt
    val hourDiff = (diffInMillis / Time.MILLIS_IN_HOUR % 24).toInt
    val minuteDiff = (diffInMillis / Time.MILLIS_IN_MINUTE % 60).toInt
    val secondDiff = (diffInMillis / Time.MILLIS_IN_SECOND % 60).toInt


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
