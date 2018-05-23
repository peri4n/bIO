name := "bIO"

lazy val commonSettings = Seq(
                               organization := "at.bioinform",
                               version := "1.1.5",
                               scalaVersion := "2.12.2",
                               isSnapshot := true,
                               resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/",
                               scalacOptions ++= Seq(
                                                      // See other posts in the series for other helpful options
                                                      "-feature",
                                                      "-language:existentials",
                                                      "-language:higherKinds",
                                                      "-language:implicitConversions"
                                                    )
                             )

/** Project dependencies */
lazy val root = project.in(file("."))
  .settings(commonSettings, coverageSettings)
  .aggregate(lucene, stream, webapp)
  .enablePlugins(CodacyCoveragePlugin)

lazy val lucene = (project in file("subprojects/lucene"))
  .settings(commonSettings, coverageSettings)

lazy val stream = (project in file("subprojects/stream"))
  .dependsOn(lucene)
  .settings(commonSettings, coverageSettings)

lazy val webapp = (project in file("subprojects/webapp"))
  .dependsOn(stream)
  .settings(commonSettings, coverageSettings)

lazy val bench = (project in file("benchmarks"))
  .dependsOn(lucene)
  .settings(commonSettings)
  .enablePlugins(JmhPlugin)

lazy val tools = (project in file("tools"))
  .dependsOn(stream)
  .settings(commonSettings)

/** Coverage analysis settings */
lazy val coverageSettings = Seq(coverageHighlighting := true)

