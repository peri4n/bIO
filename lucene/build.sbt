name := "lucene"

// lucene
libraryDependencies += "org.apache.lucene" % "lucene-core" % "6.6.0"
libraryDependencies += "org.apache.lucene" % "lucene-analyzers-common" % "6.6.0"

// test dependencies
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1"
)
