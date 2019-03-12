name := "bIO-core"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"

// test dependencies
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5",
  "org.scalacheck" %% "scalacheck" % "1.14.0"
) map { _ % Test }
