package at.bioinform.tools.indexer

import java.io.File
import java.net.URI

case class Config(
                   fastaFile: File = new File("~/test.fa"),
                   clusterUrl: URI = new URI("http://localhost:9000"))
