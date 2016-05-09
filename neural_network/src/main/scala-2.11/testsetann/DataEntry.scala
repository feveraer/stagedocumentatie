package testsetann

import java.time.LocalDateTime

import time.{DateTime, DateTimeDifference, TimeHelper}

/**
  * Created by Lorenz on 13/04/2016.
  */
case class DataEntry (time: DateTime, setTemp: Int, measuredTemp: Int) {
  def difference(entry:DataEntry): DateTimeDifference ={
    this.time.difference(entry.time)
  }
}

case class DataEntryV2 (time: LocalDateTime, setTemp: Int, measuredTemp: Int) {
  def difference(entry: DataEntryV2): Long ={
    TimeHelper.differenceBetweenInMinutes(this.time, entry.time)
  }
}
