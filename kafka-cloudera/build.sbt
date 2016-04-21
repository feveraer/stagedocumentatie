name := "kafka-cloudera"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "0.9.0.1",
  "org.slf4j" % "slf4j-log4j12" % "1.7.21",
  "com.jcraft" % "jsch" % "0.1.53",
  "org.scalactic" %% "scalactic" % "2.2.6",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

    