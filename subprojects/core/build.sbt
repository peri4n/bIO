name := "bIO-core"

// test dependencies
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1",
  "org.scalacheck" %% "scalacheck" % "1.14.0"
) map { _ % Test }
