package controllers

import java.nio.file.Paths

import akka.stream.IOResult
import akka.stream.scaladsl.{Flow, Framing, Keep}
import akka.util.ByteString
import at.bioinform.stream.fasta.{FastaEntry, FastaFlow}
import at.bioinform.stream.lucene.LuceneSink
import at.bioinform.lucene.Analyzers
import at.bioinform.lucene.segment.Segment
import at.bioinform.stream.util.Splitter
import org.apache.lucene.document.{Document, Field, TextField}
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.SimpleFSDirectory
import play.api.Logger
import play.api.libs.json._
import play.api.libs.streams.Accumulator
import play.api.mvc.{BaseController, BodyParser, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global

class LuceneController(val controllerComponents: ControllerComponents) extends BaseController {

  val logger = Logger(this.getClass)

  val bp: BodyParser[IOResult] = BodyParser { req =>
    val sink = FastaFlow(Splitter.withSize(10, 2))
      .via(Flow[Segment].map(_ => new Document()))
      .toMat(LuceneSink(new SimpleFSDirectory(Paths.get("target/database"))))(Keep.right)
    Accumulator(sink).map(Right.apply)
  }

  implicit val toJson = new Writes[Segment] {
    override def writes(o: Segment) = Json.obj(
      "id" -> o.id.string,
      "description" -> Json.parse(o.description.map(_.string).getOrElse("")),
      "sequence" -> o.sequence.string)
  }

  def add = Action(bp) { request =>
    Ok(Json.toJson(request.body.count))
  }

  def search = Action { request =>
    logger.info("Incoming request" + request)

    val sequence = request.getQueryString("sequence")
    val reader = DirectoryReader.open(new SimpleFSDirectory(Paths.get("target/database")))
    val searcher = new IndexSearcher(reader)

    val query = new QueryParser("sequence", Analyzers.ngram(6, 6)).parse(sequence.get)
    val docs = searcher.search(query, 10)
    reader.close()

    Ok(Json.obj(
      "hits" -> Json.arr(docs.scoreDocs.map(d => JsString(d.toString))),
      "total" -> JsNumber(docs.totalHits)))
  }
}
