import java.nio.file.{Files, Paths}

name := "bIO-bench"

lazy val downloadHumanGenome = taskKey[Unit]("Download the human genome and store it in test resources")

downloadHumanGenome := {
  val destination = Paths.get((resourceDirectory in Test).value.getAbsolutePath, "hg38.fa")
  if (java.nio.file.Files.notExists(destination)) {
    println("Downloading human genome ...")
    IO.gunzip(
      new URL("http://hgdownload.cse.ucsc.edu/goldenPath/hg38/bigZips/hg38.fa.gz").openStream(),
      Files.newOutputStream(destination)
    )
  } else {
    println("Genome already downloaded.")
  }
}

run in Jmh <<= (run in Jmh).dependsOn(downloadHumanGenome)

libraryDependencies += "commons-io" % "commons-io" % "2.5" % Compile
