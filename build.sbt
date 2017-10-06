name := "bIO"

lazy val commonSettings = Seq(
  organization := "at.bioinform",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.2",
  isSnapshot := true,
  resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"
)

lazy val lucene = (project in file("lucene"))
  .settings(commonSettings)

lazy val tools = (project in file("tools"))
  .dependsOn(stream)
  .settings(commonSettings)

lazy val stream = (project in file("stream"))
  .dependsOn(lucene)
  .settings(commonSettings)

lazy val bench = (project in file("bench"))
  .dependsOn(lucene)
  .settings(commonSettings)
  .enablePlugins(JmhPlugin)

lazy val webapp = (project in file("webapp"))
  .dependsOn(stream)
  .settings(commonSettings)
  .enablePlugins(PlayScala)



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
