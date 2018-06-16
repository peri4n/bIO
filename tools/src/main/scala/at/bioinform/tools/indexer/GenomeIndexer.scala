package at.bioinform.tools.indexer

import java.io.File
import java.net.URI

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.elasticsearch.IncomingMessage
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchSink
import at.bioinform.lucene.{Id, Seq}
import at.bioinform.io.fasta.{FastaEntry, FastaFlow}
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.slf4j.LoggerFactory
import spray.json.DefaultJsonProtocol._
import spray.json.{JsonFormat, _}

import scala.concurrent.ExecutionContext.Implicits.global

object GenomeIndexer {

  private[this] val Logger = LoggerFactory.getLogger(this.getClass)

  implicit val system: ActorSystem = ActorSystem("GenomeIndexer")

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val client: RestClient = RestClient.builder(new HttpHost("localhost", 9200)).build()

  implicit val format: JsonFormat[FastaEntry] = new JsonFormat[FastaEntry] {

    override def read(json: JsValue): FastaEntry = {
      val jsObject = json.asJsObject
      jsObject.getFields("id", "desc", "sequence") match {
        case scala.collection.Seq(id, _, sequence) => {
          FastaEntry(Id(id.convertTo[String]),
            Seq(sequence.convertTo[String]))
        }
      }
    }

    override def write(obj: FastaEntry): JsValue = JsObject(
      "id" -> obj.id.value.toJson,
      "sequence" -> obj.sequence.value.toJson)
  }

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
      case Some(config) => uploadFastaFileTo(config.fastaFile, config.clusterUrl)
      case None         => system.terminate()
    }
  }

  def uploadFastaFileTo(fastaFile: File, uri: URI): Unit = {
    Logger.info("Starting to upload {} to {}", List(fastaFile, uri): _*)

    val future = FastaFlow.from(fastaFile.toPath)
      .map(fastaEntry => IncomingMessage(Some(fastaEntry.id.value), fastaEntry))
      .runWith(ElasticsearchSink.create[FastaEntry](indexName = "genome",
        typeName = "sequence"))

    future.onComplete {
      _ => {
        client.close()
        system.terminate()
      }
    }
  }
}
