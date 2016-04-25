name := "neural_network"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "3.0.0-M16-SNAP3" % Test,
  "junit" % "junit" % "4.12" % Test,
  "com.quantifind" %% "wisp" % "0.0.4",
  "com.typesafe.akka" %% "akka-actor" % "2.4.4"
)