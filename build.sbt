

name := "bIO"

lazy val commonSettings = Seq(
  organization := "at.bioinform",
  version := "1.1.5",
  scalaVersion := "2.12.2",
  isSnapshot := true,
  resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/",
  scalacOptions ++= Seq(
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Ypartial-unification",
    "-Xfatal-warnings",
    "-deprecation",
    "-Xlint:missing-interpolator",
    "-Ywarn-unused:imports,locals,patvars,privates",
    "-Ywarn-dead-code"
  )
)

/** Project dependencies */
lazy val root = project.in(file("."))
  .settings(commonSettings, coverageSettings)
  .aggregate(lucene, io, webapp, core)
  .enablePlugins(CodacyCoveragePlugin)

lazy val lucene = (project in file("subprojects/lucene"))
  .settings(commonSettings, coverageSettings)

lazy val io = (project in file("subprojects/io"))
  .dependsOn(lucene)
  .settings(commonSettings, coverageSettings)

lazy val webapp = (project in file("subprojects/webapp"))
  .dependsOn(io)
  .settings(commonSettings, coverageSettings)

lazy val core = (project in file("subprojects/core"))
  .settings(commonSettings, coverageSettings)

lazy val bench = (project in file("benchmarks"))
  .dependsOn(lucene)
  .settings(commonSettings)
  .enablePlugins(JmhPlugin)

lazy val tools = (project in file("tools"))
  .dependsOn(io)
  .settings(commonSettings)

/** Coverage analysis settings */
lazy val coverageSettings = Seq(coverageHighlighting := true)

