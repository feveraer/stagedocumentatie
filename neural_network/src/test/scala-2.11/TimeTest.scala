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

    val diff1 = time1.difference(time2)
    val diff2 = time1.difference(time3)
    val diff3 = time4.difference(time5)
    val diff4 = time6.difference(time1)
    val diff5 = time7.difference(time1)

    assertEquals(0, diff1.hour)
    assertEquals(51, diff1.minute)
    assertEquals(0, diff2.hour)
    assertEquals(10, diff2.minute)
    assertEquals(1, diff3.hour)
    assertEquals(10, diff3.minute)
    assertEquals(0, diff4.hour)
    assertEquals(20, diff4.minute)
    assertEquals(0, diff5.hour)
    assertEquals(30, diff5.minute)
  }
}
