name := "bIO-webapp"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10"
)

libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.6.7"

/** Coverage analysis settings */
coverageExcludedPackages := "<empty>;.*router.*;.*views.html.*;.*controllers.javascript.*"
