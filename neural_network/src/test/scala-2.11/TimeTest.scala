import org.scalatest.junit.AssertionsForJUnit
import org.junit.Assert._
import org.junit.Test
import testsetann.Time

/**
  * Created by Lorenz on 13/04/2016.
  */

class TimeTest extends AssertionsForJUnit {
  @Test def testDifference(): Unit ={
    val time1 = new Time(23,10)
    val time2 = new Time(0,1)
    val time3 = new Time(23,20)
    val time4 = new Time(22,30)
    val time5 = new Time(23,40)
    val time6 = new Time(22,50)
    val time7 = new Time(22,40)
    val time8 = new Time(23,9)
    val time9 = new Time(22,9)

    val diff1 = time1.difference(time2)
    val diff2 = time1.difference(time3)
    val diff3 = time4.difference(time5)
    val diff4 = time6.difference(time1)
    val diff5 = time7.difference(time1)
    val diff6 = time1.difference(time8)
    val diff7 = time1.difference(time9)

    assertEquals(0, diff1.hour)
    assertEquals(51, diff1.minutes)
    assertEquals(0, diff2.hour)
    assertEquals(10, diff2.minutes)
    assertEquals(1, diff3.hour)
    assertEquals(10, diff3.minutes)
    assertEquals(0, diff4.hour)
    assertEquals(20, diff4.minutes)
    assertEquals(0, diff5.hour)
    assertEquals(30, diff5.minutes)
    assertEquals(23, diff6.hour)
    assertEquals(59, diff6.minutes)
    assertEquals(22, diff7.hour)
    assertEquals(59, diff7.minutes)
  }
}
