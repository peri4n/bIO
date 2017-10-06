package at.bioinform.tools

import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Framing}
import akka.util.ByteString
import at.bioinform.codec.fasta.FastaFlow
import at.bioinform.codec.lucene.LuceneSink
import org.apache.lucene.document.{Document, Field, TextField}
import org.apache.lucene.store.MMapDirectory
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global

object GenomeIndexer {

  private val Logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    assert(args.length == 2)

    val fastaFile = new File(args(0)).toPath
    Logger.info("Starting to index: {} ", fastaFile)

    val outputFile = new File(args(1)).toPath
    Logger.info("Index will be stored at {} ", outputFile)

    implicit val system = ActorSystem("GenomeIndexer")

    implicit val materializer = ActorMaterializer()

    val index = new MMapDirectory(outputFile)

    val future = FileIO.fromPath(fastaFile)
      .via(Framing.delimiter(ByteString(System.lineSeparator()), 200))
      .via(FastaFlow)
      .runWith(LuceneSink(index, entry => {
        Logger.info("Indexing {} ", entry.header.id)
        val document = new Document()
        document.add(new Field("id", entry.header.id, TextField.TYPE_STORED))
        document.add(new Field("sequence", entry.sequence, TextField.TYPE_STORED))
        document
      }))

    future.onComplete {
      _ => system.terminate()
    }
  }

}
