import java.nio.charset.StandardCharsets

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.util.ByteString

import scala.collection.mutable

class FastaProcessor extends GraphStage[FlowShape[ByteString, String]] {
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    private var stringBuilder = StringBuilder.newBuilder

    private var queue = new mutable.Queue[String]()

    setHandler(in, new InHandler {
      override def onPush(): Unit = {
        val chunk = grab(in).decodeString(StandardCharsets.UTF_8).filterNot(Character.isWhitespace)
        stringBuilder ++= chunk
        while (stringBuilder.count(_ == '>') > 1) {
          val (seq, rest) = stringBuilder.tail.span(_ != '>')
          stringBuilder = rest
          queue.enqueue(seq.toString())
        }
        push(out, queue.dequeue)
      }

      override def onUpstreamFinish(): Unit = {
        push(out, stringBuilder.result())
        super.onUpstreamFinish()
      }
    })

    setHandler(out, new OutHandler {
      override def onPull(): Unit =
        if (queue.isEmpty) {
          pull(in)
        } else {
          push(out, queue.dequeue)
        }
    })

  }

  val in = Inlet[ByteString]("input")
  val out = Outlet[String]("output")

  override def shape: FlowShape[ByteString, String] = FlowShape(in, out)
}
