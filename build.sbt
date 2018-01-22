name := "bIO"

lazy val commonSettings = Seq(
  organization := "at.bioinform",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.3",
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
lazy val root = (project in file("."))
  .aggregate(lucene, stream, webapp)
  .settings(commonSettings, coverageSettings)
  .enablePlugins(CodacyCoveragePlugin)

lazy val lucene = (project in file("subprojects/lucene"))
  .settings(commonSettings, coverageSettings)

lazy val tools = (project in file("tools"))
  .dependsOn(stream)
  .settings(commonSettings)

lazy val stream = (project in file("subprojects/stream"))
  .dependsOn(lucene)
  .settings(commonSettings, coverageSettings)

lazy val bench = (project in file("benchmarks"))
  .dependsOn(lucene)
  .settings(commonSettings)
  .enablePlugins(JmhPlugin)

lazy val webapp = (project in file("subprojects/webapp"))
  .dependsOn(stream)
  .settings(commonSettings, coverageSettings)


/** Scalariform settings */
scalariformSettings(true)

com.typesafe.sbt.SbtScalariform.ScalariformKeys.preferences := {
  import scalariform.formatter.preferences._
  FormattingPreferences()
    .setPreference(AlignArguments, true)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 20)
    .setPreference(DanglingCloseParenthesis, Prevent)
    .setPreference(CompactControlReadability, false)
    .setPreference(SpacesAroundMultiImports, false)
}

/** Coverage analysis settings */
lazy val coverageSettings = Seq(
  coverageHighlighting := true
)

