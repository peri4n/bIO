name := "at/bioinform/lucene"

// lucene
libraryDependencies += "org.apache.lucene" % "lucene-core" % "6.6.0"
libraryDependencies += "org.apache.lucene" % "lucene-analyzers-common" % "6.6.0"
libraryDependencies += "org.apache.lucene" % "lucene-queryparser" % "6.6.0"
libraryDependencies += "org.apache.lucene" % "lucene-highlighter" % "6.6.0"

// test dependencies
libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.0.1" % Test)

javaOptions ++= Seq("-Xms512M", "-Xmx4096M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
