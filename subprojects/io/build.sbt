name := "bIO-io"

// akka
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.3"

// lucene
libraryDependencies += "org.apache.lucene" % "lucene-core" % "6.6.0"
libraryDependencies += "org.apache.lucene" % "lucene-analyzers-common" % "6.6.0"

// cats
libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"

// test dependencies
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.3",
  "org.scalacheck" %% "scalacheck" % "1.14.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.3") map {
  _ % Test
}
