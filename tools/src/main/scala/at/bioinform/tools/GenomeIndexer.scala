package at.bioinform.tools

import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import at.bioinform.stream.fasta.{FastaEntry, FastaFlow}
import at.bioinform.stream.lucene.LuceneSink
import org.apache.lucene.document.Document
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

    val future = FastaFlow.from(fastaFile)
      .via(Flow[FastaEntry].map(_ => new Document()))
      .runWith(LuceneSink(index))

    future.onComplete {
      _ => system.terminate()
    }
  }

}
