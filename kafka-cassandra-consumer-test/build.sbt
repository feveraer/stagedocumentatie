name := "kafka-cassandra-consumer-test"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "0.9.0.1",
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.0"
)