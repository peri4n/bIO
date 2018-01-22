package at.bioinform.tools.indexer

import java.io.File
import java.net.URI

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import at.bioinform.stream.fasta.FastaFlow
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global

object GenomeIndexer {

  private val Logger = LoggerFactory.getLogger(this.getClass)

  implicit val system: ActorSystem = ActorSystem("GenomeIndexer")

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def main(args: Array[String]): Unit = {
    val parser = new scopt.OptionParser[Config]("genomeIndexer") {
      head("genomeIndexer", "0.1")

      opt[File]('i', "file")
        .required()
        .valueName("<file>")
        .action((x, c) => c.copy(fastaFile = x))
        .text("the fasta file to upload")
      opt[URI]('h', "host")
        .required()
        .valueName("<host>")
        .action((x, c) => c.copy(clusterUrl = x))
        .text("the host the fasta file gets uploaded to")
    }

    // parser.parse returns Option[C]
    parser.parse(args, Config()) match {
      case Some(config) =>
        uploadFastaFileTo(config.fastaFile, config.clusterUrl)
      case None =>
        system.terminate()
    }
  }

  def uploadFastaFileTo(fastaFile: File, uri: URI): Unit = {
    Logger.info("Starting to upload {} to {}", List(fastaFile, uri): _*)

    val future = FastaFlow.from(fastaFile.toPath)
      .runWith(Sink.foreach(println))

    future.onComplete {
      _ => system.terminate()
    }
  }

}
