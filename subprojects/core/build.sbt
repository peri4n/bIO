name := "bIO-core"

// test dependencies
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1"
) map { _ % Test }
