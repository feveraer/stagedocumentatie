name := "scala-app"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.1",
  "org.apache.spark" %% "spark-sql" % "1.6.1",
  "com.databricks" %% "spark-csv" % "1.4.0",
  "com.quantifind" %% "wisp" % "0.0.4"
)