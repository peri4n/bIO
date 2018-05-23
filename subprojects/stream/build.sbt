name := "bIO-stream"

// akka
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.3"

// lucene
libraryDependencies += "org.apache.lucene" % "lucene-core" % "6.6.0"
libraryDependencies += "org.apache.lucene" % "lucene-analyzers-common" % "6.6.0"

// test dependencies
libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.0.1",
                            "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.3",
                            "com.typesafe.akka" %% "akka-testkit" % "2.5.3") map {_ % Test}
