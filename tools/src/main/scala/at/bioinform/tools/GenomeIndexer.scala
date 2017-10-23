package at.bioinform.tools

import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow, Framing}
import akka.util.ByteString
import at.bioinform.lucene.segment.Segment
import at.bioinform.stream.fasta.FastaFlow
import at.bioinform.stream.lucene.LuceneSink
import at.bioinform.stream.util.Splitter
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

    val future = FastaFlow.from(fastaFile, Splitter.noop)
      .via(Flow[Segment].map(_ => new Document()))
      .runWith(LuceneSink(index))

    future.onComplete {
      _ => system.terminate()
    }
  }

}
