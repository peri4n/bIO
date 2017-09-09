package controllers

import javax.inject.Inject

import akka.stream.scaladsl.{Flow, Keep, Sink}
import akka.util.ByteString
import at.bioinform.codec.{FastaEntry, FastaFlow}
import play.api.libs.streams.Accumulator
import play.api.mvc.{BaseController, BodyParser, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global

class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  val bp: BodyParser[List[FastaEntry]] = BodyParser { req =>
    val sink = Flow[ByteString]
      .via(FastaFlow)
      .toMat(Sink.fold(List.empty[FastaEntry])(_ :+ _))(Keep.right)
    Accumulator(sink).map(Right.apply)
  }

  def upload = Action(bp) { request =>
    Ok(s"File uploaded: ${request.body.mkString("\n")}")
  }

}
