name := "bIO-webapp"

routesGenerator := InjectedRoutesGenerator

libraryDependencies += "org.apache.lucene" % "lucene-queryparser" % "6.6.0"

/** Coverage analysis settings */
coverageExcludedPackages := "<empty>;.*router.*;.*views.html.*;.*controllers.javascript.*"
