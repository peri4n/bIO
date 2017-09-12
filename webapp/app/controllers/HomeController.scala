package controllers

import javax.inject.Inject

import akka.stream.scaladsl.{Flow, Framing, Keep, Sink}
import akka.util.ByteString
import at.bioinform.codec.fasta.{FastaEntry, FastaFlow}
import play.api.Logger
import play.api.libs.json.{Json, Writes}
import play.api.libs.streams.Accumulator
import play.api.mvc.{BaseController, BodyParser, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global

class HomeController @Inject() (val controllerComponents: ControllerComponents) extends BaseController {

  val logger: Logger = Logger(this.getClass())

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  val bp: BodyParser[List[FastaEntry]] = BodyParser { req =>
    logger.info("Incoming requst: " + req)
    val sink = Flow[ByteString]
      .via(Framing.delimiter(ByteString(System.lineSeparator()), 200))
      .via(FastaFlow)
      .toMat(Sink.fold(List.empty[FastaEntry])(_ :+ _))(Keep.right)
    Accumulator(sink).map(Right.apply)
  }

  implicit val toJson = new Writes[FastaEntry] {
    override def writes(o: FastaEntry) = Json.obj(
      "id" -> o.header.id,
      "description" -> o.header.description,
      "sequence" -> o.sequence)
  }

  def upload = Action(bp) { request =>
    Ok(Json.toJson(request.body))
  }

}
