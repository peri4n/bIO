name := "bIO-webapp"

routesGenerator := InjectedRoutesGenerator

libraryDependencies += "org.apache.lucene" % "lucene-queryparser" % "6.6.0"

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test
)

/** Coverage analysis settings */
coverageExcludedPackages := "<empty>;.*router.*;.*views.html.*;.*controllers.javascript.*"
