name := "bIO-webapp"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10"
)

libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.6.7"

libraryDependencies += "com.typesafe" % "config" % "1.3.1"

// logging
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

/** Coverage analysis settings */
coverageExcludedPackages := "<empty>;.*router.*;.*views.html.*;.*controllers.javascript.*"
