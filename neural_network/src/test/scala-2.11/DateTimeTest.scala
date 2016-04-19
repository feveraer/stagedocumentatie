import org.scalatest.junit.AssertionsForJUnit
import org.junit.Assert._
import org.junit.Test
import testsetann.{DateTime, DateTimeDifference}

/**
  * Created by Lorenz on 13/04/2016.
  */

class DateTimeTest extends AssertionsForJUnit {
  @Test def testSecondsDifference(): Unit = {
    val time1 = new DateTime(2015, 12, 31, 23, 10, 0)
    val time2 = new DateTime(2015, 12, 31, 23, 10, 10)
    val time3 = new DateTime(2015, 12, 31, 23, 9, 50)

    val time4 = new DateTime(2015, 12, 31, 23, 10, 1)
    val time5 = new DateTime(2015, 12, 31, 23, 10, 11)
    val time6 = new DateTime(2015, 12, 31, 23, 9, 51)

    val solution1 = new DateTimeDifference(0, 0, 0, 10)

    val diff1 = time1.difference(time2)
    val diff2 = time3.difference(time1)

    val diff3 = time4.difference(time5)
    val diff4 = time6.difference(time4)


    assertEquals(solution1, diff1)
    assertEquals(solution1, diff2)

    assertEquals(solution1, diff3)
    assertEquals(solution1, diff4)
  }

  @Test def testMinutesDifference(): Unit = {
    val time1 = new DateTime(2015, 12, 31, 23, 0, 0)
    val time2 = new DateTime(2015, 12, 31, 23, 10, 0)
    val time3 = new DateTime(2015, 12, 31, 22, 50, 0)

    val time4 = new DateTime(2015, 12, 31, 23, 1, 0)
    val time5 = new DateTime(2015, 12, 31, 23, 11, 0)
    val time6 = new DateTime(2015, 12, 31, 22, 51, 0)

    val solution1 = new DateTimeDifference(0, 0, 10, 0)

    val diff1 = time1.difference(time2)
    val diff2 = time3.difference(time1)

    val diff3 = time4.difference(time5)
    val diff4 = time6.difference(time4)


    assertEquals(solution1, diff1)
    assertEquals(solution1, diff2)

    assertEquals(solution1, diff3)
    assertEquals(solution1, diff4)
  }

  @Test def testHourDifference(): Unit = {
    val time1 = new DateTime(2015, 12, 15, 17, 0, 0)
    val time2 = new DateTime(2015, 12, 15, 19, 0, 0)
    val time3 = new DateTime(2015, 12, 15, 15, 0, 0)

    val time4 = new DateTime(2015, 12, 15, 23, 0, 0)
    val time5 = new DateTime(2015, 12, 16, 1, 0, 0)
    val time6 = new DateTime(2015, 12, 15, 21, 0, 0)

    val solution1 = new DateTimeDifference(0, 2, 0, 0)

    val diff1 = time1.difference(time2)
    val diff2 = time3.difference(time1)

    val diff3 = time4.difference(time5)
    val diff4 = time6.difference(time4)


    assertEquals(solution1, diff1)
    assertEquals(solution1, diff2)

    assertEquals(solution1, diff3)
    assertEquals(solution1, diff4)
  }

  @Test def testDayDifference(): Unit = {
    val time1 = new DateTime(2015, 12, 15, 0, 0, 0)
    val time2 = new DateTime(2015, 12, 17, 0, 0, 0)
    val time3 = new DateTime(2015, 12, 13, 0, 0, 0)

    val time4 = new DateTime(2015, 10, 30, 0, 0, 0)
    val time5 = new DateTime(2015, 11, 1, 0, 0, 0)

    val time6 = new DateTime(2015, 9, 30, 0, 0, 0)
    val time7 = new DateTime(2015, 10, 2, 0, 0, 0)

    val time8 = new DateTime(2015, 2, 28, 0, 0, 0)
    val time9 = new DateTime(2015, 3, 2, 0, 0, 0)

    val time10 = new DateTime(2016, 2, 28, 0, 0, 0)
    val time11 = new DateTime(2016, 3, 1, 0, 0, 0)

    val solution1 = new DateTimeDifference(2, 0, 0, 0)

    val diff1 = time1.difference(time2)
    val diff2 = time3.difference(time1)

    val diff3 = time4.difference(time5)

    val diff4 = time6.difference(time7)

    val diff5 = time8.difference(time9)

    val diff6 = time10.difference(time11)


    assertEquals(solution1, diff1)
    assertEquals(solution1, diff2)

    assertEquals(solution1, diff3)

    assertEquals(solution1, diff4)

    assertEquals(solution1, diff5)

    assertEquals(solution1, diff6)
  }

  @Test def testRandomDifference(): Unit = {
    val time1 = new DateTime(2017, 10, 2, 10, 12, 50)
    val time2 = new DateTime(2016, 10, 1, 0, 0, 0)

    val solution1 = new DateTimeDifference(366, 10, 12, 50)

    val diff1 = time2.difference(time1)

    assertEquals(solution1, diff1)

    val time3 = new DateTime(50, 10, 2, 10, 12, 50)
    val time4 = new DateTime(51, 10, 1, 0, 0, 0)
    val solution2 = new DateTimeDifference(363, 13, 47, 10)

    val diff2 = time3.difference(time4)

    assertEquals(solution2, diff2)

    val time5 = new DateTime(2011, 9, 21, 8, 33, 0)
    val time6 = new DateTime(2011, 9, 23, 16, 5, 0)
    val solution3 = new DateTimeDifference(2, 7, 32, 0)

    val diff3 = time5.difference(time6)

    assertEquals(solution3, diff3)
  }

  @Test def testTimeDifferenceToMillis(): Unit = {
    val td1 = new DateTimeDifference(1,0,0,0)
    val solution1 = 86400000
    assertEquals(solution1, td1.toMillis())

    val td2 = new DateTimeDifference(0,1,0,0)
    val solution2 = 3600000
    assertEquals(solution2,td2.toMillis())

    val td3 = new DateTimeDifference(0,0,1,0)
    val solution3 = 60000
    assertEquals(solution3, td3.toMillis())

    val td4 = new DateTimeDifference(0,0,0,1)
    val solution4 = 1000
    assertEquals(solution4,td4.toMillis())
  }

}
