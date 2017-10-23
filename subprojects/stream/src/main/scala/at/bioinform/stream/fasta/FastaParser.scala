package at.bioinform.stream.fasta

import java.nio.charset.StandardCharsets

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.util.ByteString
import at.bioinform.lucene.segment.Segment
import at.bioinform.lucene.{Desc, Id, Pos}
import at.bioinform.stream.util.Splitter

import scala.util.{Failure, Success, Try}

/**
 * Flow that parses an incoming byte stream representing FASTA-formatted sequence data.
 *
 * Note: This flow expects it's income in chunks of lines!!!
 */
private[fasta] case class FastaParser(splitter: Splitter) extends GraphStage[FlowShape[ByteString, Segment]] {

  import FastaParser._

  val in: Inlet[ByteString] = Inlet[ByteString]("input")
  val out: Outlet[Segment] = Outlet[Segment]("output")

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    private var sequenceBuilder = new StringBuilder(1000)

    /** Last parded header. */
    private var header: Option[Header] = None

    /** Offset in the current sequence. */
    private var offset: Pos = Pos(0)

    setHandler(in, new InHandler {

      override def onPush(): Unit = {
        val line = grab(in).decodeString(StandardCharsets.UTF_8).trim
        if (!isHeaderLine(line) && header.isEmpty) {
          // TODO add error handling
          // the first non-comment line is not a header
        } else {
          if (isHeaderLine(line)) {
            if (header.isEmpty) { // first header line
              header = Some(parseHeader(line).get) // TODO add error handling
              pull(in)
            } else {
              val (builder, sequence) = splitter.split(sequenceBuilder)
              offset += sequence.length
              sequenceBuilder = builder
              push(out, Segment(header.get._1, sequence, header.get._2, None))
              header = Some(parseHeader(line).get) // TODO add error handling
            }
          } else { // a sequence line
            if (splitter.willSplit(sequenceBuilder)) {
              val (builder, sequence) = splitter.split(sequenceBuilder)
              offset += sequence.length
              sequenceBuilder = builder
              push(out, Segment(header.get._1, sequence, header.get._2, None))
            }
            sequenceBuilder ++= line
            pull(in)
          }
        }
      }

      override def onUpstreamFinish(): Unit = {
        if (sequenceBuilder.nonEmpty) {
          val (builder, sequence) = splitter.split(sequenceBuilder)
          offset += sequence.length
          sequenceBuilder = builder
          push(out, Segment(header.get._1, sequence, header.get._2, None))
        }
        super.onUpstreamFinish()
      }
    })

    setHandler(out, new OutHandler {
      override def onPull(): Unit =
        pull(in)
    })

  }

  override def shape: FlowShape[ByteString, Segment] = FlowShape(in, out)
}

object FastaParser {

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
