package controllers

import java.nio.file.Paths
import javax.inject.Inject

import akka.stream.scaladsl.{Flow, Framing, Keep}
import akka.util.ByteString
import at.bioinform.codec.fasta.{FastaEntry, FastaFlow}
import at.bioinform.codec.lucene.LuceneSink
import at.bioinform.lucene.Util
import org.apache.lucene.document.{Document, Field, TextField}
import org.apache.lucene.index.{DirectoryReader, IndexReader, Term}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{IndexSearcher, TermQuery}
import org.apache.lucene.store.SimpleFSDirectory
import play.api.Logger
import play.api.libs.json.{JsNumber, JsString, Json, Writes}
import play.api.libs.streams.Accumulator
import play.api.mvc.{BaseController, BodyParser, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global

class LuceneController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  val logger = Logger(this.getClass)

  val bp: BodyParser[List[String]] = BodyParser { req =>
    val sink = Flow[ByteString]
      .via(Framing.delimiter(ByteString(System.lineSeparator()), 200))
      .via(FastaFlow)
      .toMat(LuceneSink(new SimpleFSDirectory(Paths.get("target/database")), entry => {
        val document = new Document()
        document.add(new Field("id", entry.header.id, TextField.TYPE_STORED))
        document.add(new Field("sequence", entry.sequence, TextField.TYPE_STORED))
        document
      }))(Keep.right)
    Accumulator(sink).map(Right.apply)
  }

  implicit val toJson = new Writes[FastaEntry] {
    override def writes(o: FastaEntry) = Json.obj(
      "id" -> o.header.id,
      "description" -> o.header.description,
      "sequence" -> o.sequence)
  }

  def add = Action(bp) { request =>
    Ok(Json.toJson(request.body))
  }

  def search = Action { request =>
    logger.info("Incoming request" + request)

    val sequence = request.getQueryString("sequence")
    val reader = DirectoryReader.open(new SimpleFSDirectory(Paths.get("target/database")))
    val searcher = new IndexSearcher(reader)

    val query = new QueryParser("sequence", Util.analyzer).parse(sequence.get)
    val docs = searcher.search(query, 10)
    reader.close()

    Ok(Json.obj(
      "hits" -> Json.arr(docs.scoreDocs.map(d => JsString(d.toString))),
      "total" -> JsNumber(docs.totalHits)
    ))
  }
}
