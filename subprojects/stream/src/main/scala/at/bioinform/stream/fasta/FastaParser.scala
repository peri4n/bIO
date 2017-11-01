package at.bioinform.stream.fasta

import java.nio.charset.StandardCharsets

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.util.ByteString
import at.bioinform.lucene.{Desc, Id, Seq}

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/**
 * Flow that parses an incoming byte stream representing FASTA-formatted sequence data.
 *
 * Note: This flow expects it's income in chunks of lines!!!
 */
private[fasta] object FastaParser extends GraphStage[FlowShape[ByteString, FastaEntry]] {

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    private var sequenceBuilder = new mutable.StringBuilder(1000)

    private var currentHeader: Option[Header] = None

    setHandler(in, new InHandler {

      override def onPush(): Unit = {
        val line = grab(in).decodeString(StandardCharsets.UTF_8).trim
        if (line.isEmpty || line.startsWith("#")) {
          pull(in)
        } else {
          if (isHeaderLine(line) && currentHeader.isEmpty) { // first header line
            currentHeader = Some(parseHeader(line).get)
            pull(in)
          } else if (isHeaderLine(line) && currentHeader.isDefined) {
            val (id, desc) = currentHeader.get
            push(out, FastaEntry(id, desc, Seq(sequenceBuilder.result())))
            sequenceBuilder.clear()
            currentHeader = Some(parseHeader(line).get)
          } else { // a sequence file
            sequenceBuilder ++= line
            pull(in)
          }
        }
      }

      override def onUpstreamFinish(): Unit = {
        if (sequenceBuilder.nonEmpty) {
          val (id, desc) = currentHeader.get
          push(out, FastaEntry(id, desc, Seq(sequenceBuilder.result())))
        }
        super.onUpstreamFinish()
      }
    })

    setHandler(out, new OutHandler {
      override def onPull(): Unit =
        pull(in)
    })

  }

  val in: Inlet[ByteString] = Inlet[ByteString]("input")
  val out: Outlet[FastaEntry] = Outlet[FastaEntry]("output")

  override def shape: FlowShape[ByteString, FastaEntry] = FlowShape(in, out)

  type Header = (Id, Option[Desc])

  def parseHeader(chunk: String): Try[Header] =
    if (!chunk.startsWith(">")) {
      Failure(new RuntimeException(s"Expected a '>' at the start of a fasta entry but read: '${chunk.head}'"))
    } else {
      val (id, description) = chunk.tail.span(_ != ' ')
      val maybeDescription = if (description.isEmpty) None else Some(Desc(description.trim()))
      Success((Id(id), maybeDescription))
    }

  private def isHeaderLine(line: String): Boolean = line.startsWith(">")
}
