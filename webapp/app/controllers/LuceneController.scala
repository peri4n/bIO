package controllers

import javax.inject.Inject

import akka.stream.SinkShape
import akka.stream.scaladsl.{Broadcast, Framing, GraphDSL, Sink}
import akka.util.ByteString
import at.bioinform.codec.fasta.{FastaEntry, FastaFlow}
import at.bioinform.codec.lucene.LuceneSink
import org.apache.lucene.document.{Document, Field, TextField}
import org.apache.lucene.store.RAMDirectory
import play.api.Logger
import play.api.libs.json.{Json, Writes}
import play.api.libs.streams.Accumulator
import play.api.mvc.{BaseController, BodyParser, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LuceneController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  val logger = Logger(this.getClass())

  val bp: BodyParser[List[FastaEntry]] = BodyParser { req =>
    logger.info("Incoming requst: " + req)

    val sink: Sink[FastaEntry, Future[List[FastaEntry]]] = Sink.fold(List.empty[FastaEntry])(_ :+ _)

    val g = Sink.fromGraph(GraphDSL.create(sink) { implicit builder =>
      import GraphDSL.Implicits._
      sink =>

        val splitter = builder.add(Framing.delimiter(ByteString(System.lineSeparator()), 200))
        val fasta = builder.add(FastaFlow)
        val lucene = builder.add(LuceneSink(new RAMDirectory(), entry => {
          val document = new Document()
          document.add(new Field("id", entry.header.id, TextField.TYPE_STORED))
          document.add(new Field("sequence", entry.sequence, TextField.TYPE_STORED))
          document
        }))
        val bcast = builder.add(Broadcast[FastaEntry](2))

        splitter ~> fasta ~> bcast ~> sink.in
        bcast ~> lucene
        SinkShape(splitter.in)
    })

    Accumulator(g).map(Right.apply)
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

}
