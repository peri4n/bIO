name := "bIO-webapp"

libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-http" % "10.0.10")

libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.6.7"

libraryDependencies += "com.typesafe" % "config" % "1.3.1"

// logging
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// slick
libraryDependencies ++= Seq("com.typesafe.slick" %% "slick" % "3.2.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
  "com.h2database" % "h2" % "1.4.196")

libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1"

/** Coverage analysis settings */
coverageExcludedPackages := "<empty>;.*router.*;.*views.html.*;.*controllers.javascript.*"
