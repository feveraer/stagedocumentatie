name := "scala-app"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.1",
  "com.databricks" % "spark-csv_2.11" % "1.4.0"
)