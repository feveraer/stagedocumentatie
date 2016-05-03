package helpers

import java.time.{LocalDate, LocalTime}

/**
  * Created by Lorenz on 3/05/2016.
  */
object Helpers {
  object Season {
    def getSeason(date: LocalDate): String = {
      val month = date.getMonthValue
      val day = date.getDayOfMonth

      if (month < 4) {
        return "WINTER"
      }
      if (month < 7) {
        return "SPRING"
      }
      if (month < 10){
        return "SUMMER"
      }
      "FALL"
    }
  }

  object Quartile {
    def getQuartile(time: LocalTime): Int = {
      val minutes = time.getMinute

      if (minutes < 15){
        return 1
      }

      if (minutes < 30){
        return 2
      }

      if (minutes < 45){
        return 3
      }
      4
    }
  }
}
