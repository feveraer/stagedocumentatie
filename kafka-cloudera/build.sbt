name := "kafka-cloudera"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "0.9.0.1",
  "com.jcraft" % "jsch" % "0.1.53"
)

    