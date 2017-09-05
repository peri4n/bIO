name := "bIO-codec"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.3"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.3",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.3") map { _ % "test" }
