name := "bIO"

lazy val commonSettings = Seq(
  organization := "at.bioinform",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.2",
  isSnapshot := true,
  resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"
)

lazy val codec = (project in file("codec")).settings(
  commonSettings
)

lazy val webapp = (project in file("webapp")).settings(
  commonSettings
)
