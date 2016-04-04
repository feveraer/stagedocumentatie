import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, LogManager}
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql.SQLContext

object LinearRegressionWithElasticNetExample {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("LinearRegressionWithElasticNetExample")
    val sc = new SparkContext(conf)
    val sqlCtx = new SQLContext(sc)

    LogManager.getRootLogger.setLevel(Level.OFF)

    // $example on$
    // Load training data
    val training = sqlCtx.read.format("libsvm")
      .load("src/main/resources/sample_linear_regression_data.txt")

    val lr = new LinearRegression()
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)

    // Fit the model
    val lrModel = lr.fit(training)

    // Print the coefficients and intercept for linear regression
    println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

    // Summarize the model over the training set and print out some metrics
    val trainingSummary = lrModel.summary
    println(s"numIterations: ${trainingSummary.totalIterations}")
    println(s"objectiveHistory: ${trainingSummary.objectiveHistory.toList}")
    trainingSummary.residuals.show()
    println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    println(s"r2: ${trainingSummary.r2}")
    // $example off$

    sc.stop()
  }
}