package at.bioinform.codec

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import akka.stream._
import akka.stream.scaladsl.{FileIO, Framing, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
 * Flow that parses an incoming byte stream representing FASTA-formatted sequence data.
 */
object FastaFlow extends GraphStage[FlowShape[ByteString, FastaEntry]] {

  val MAX_LINE_SIZE = 200

  /**
   * Utility method to easily create a processor from a given path.
   *
   * @param path path to a FASTA formatted file.
   * @return a flow providing [[FastaEntry]]s
   */
  def from(path: Path): Source[FastaEntry, Future[IOResult]] = {
    FileIO.fromPath(path)
      .via(Framing.delimiter(ByteString(System.lineSeparator()), MAX_LINE_SIZE))
      .via(FastaFlow)
      .named("FastaFlow")
  }

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    private var sequenceBuilder = new StringBuilder(1000)

    private var currentHeader: Option[FastaHeader] = None

    def parseHeader(chunk: String): Try[FastaHeader] =
      if (!chunk.startsWith(">")) {
        Failure(new RuntimeException(s"Expected a '>' at the start of a fasta entry but read: '${chunk.head}'"))
      } else {
        val (id, description) = chunk.tail.span(_ != ' ')
        val maybeDescription = if (description.isEmpty) None else Some(description.trim())
        Success(FastaHeader(id, maybeDescription))
      }

    private def isHeaderLine(line: String): Boolean = line.startsWith(">")

    setHandler(in, new InHandler {

      override def onPush(): Unit = {
        val line = grab(in).decodeString(StandardCharsets.UTF_8).trim
        if (isHeaderLine(line) && currentHeader.isEmpty) { // first header line
          currentHeader = Some(parseHeader(line).get)
          pull(in)
        } else if (isHeaderLine(line) && currentHeader.isDefined) {
          push(out, FastaEntry(currentHeader.get, sequenceBuilder.result()))
          sequenceBuilder.clear()
          currentHeader = Some(parseHeader(line).get)
        } else { // a sequence file
          sequenceBuilder ++= line
          pull(in)
        }
      }

      override def onUpstreamFinish(): Unit = {
        if (sequenceBuilder.nonEmpty) {
          push(out, FastaEntry(currentHeader.get, sequenceBuilder.result()))
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
}
