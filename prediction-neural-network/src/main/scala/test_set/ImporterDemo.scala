package test_set

/**
  * Created by Lorenz on 5/04/2016.
  */
object ImporterDemo {
  def main(args: Array[String]) {
    val testSetCreator = new TestSetCreator

    testSetCreator.createTestSets()
  }
}
