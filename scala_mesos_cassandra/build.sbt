name := "scala_mesos_cassandra"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.0",
  "com.jcraft" % "jsch" % "0.1.53"
)