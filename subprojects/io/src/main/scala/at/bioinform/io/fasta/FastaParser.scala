package at.bioinform.io.fasta

import java.nio.charset.StandardCharsets

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.util.ByteString
import at.bioinform.lucene.{Id, Seq}
import cats.data.StateT
import cats.instances.try_._

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/**
 * Flow that parses an incoming byte stream representing FASTA-formatted sequence data.
 *
 * Note: This flow expects it's income in chunks of lines!!!
 */
private[fasta] object FastaParser extends GraphStage[FlowShape[ByteString, FastaEntry]] {

  type FastaStep[A] = StateT[Try, State, A]

  case class State(buffer: mutable.StringBuilder, headerEnd: Int = -1, sequenceEnd: Int = -1, cursor: Int = 0) {

    def isEmpty = buffer.isEmpty

    def appendLine(line: String) = copy(buffer = buffer.append(line))

    def startsWithHeader: Boolean = buffer.startsWith(FastaHeaderStart)

    def setEndOfHeader() =
      if (headerEnd == -1) {
        if (startsWithHeader) {
          val endOfHeader = buffer.indexOf(System.lineSeparator())
          Success(copy(headerEnd = endOfHeader, cursor = endOfHeader))
        } else {
          Failure(FastaParserException(s"Expected $FastaHeaderStart"))
        }
      } else {
        Success(this.copy(cursor = buffer.length))
      }

    def setEndOfSequence(greedy: Boolean) = {
      if (greedy) {
        copy(sequenceEnd = buffer.length, cursor = 0)
      } else {
        copy(sequenceEnd = math.max(buffer.length, buffer.indexOf(FastaHeaderStart, cursor)), cursor = buffer.length)
      }
    }

    def extractEntry: Option[(String, String)] = {
      if (sequenceEnd == -1) {
        None
      } else {
        val header = buffer.substring(1, headerEnd)
        val sequence = buffer.substring(headerEnd + 1, sequenceEnd)
        buffer.clear()
        Some((header, sequence))
      }
    }
  }

  val in: Inlet[ByteString] = Inlet[ByteString]("input")

  val out: Outlet[FastaEntry] = Outlet[FastaEntry]("output")

  val FastaHeaderStart = ">"

  val header: FastaStep[Unit] = StateT.modifyF { _.setEndOfHeader() }

  /**
   * Parsing step to extract the entire sequence of an input.
   *
   * The returned sequence is represented as an optional string because
   * the sequence is only extracted completely. If no new Fasta entry is
   * seen, we can be sure that the sequence is complete.
   */
  def sequence(greedy: Boolean): FastaStep[Unit] = StateT.modify { _.setEndOfSequence(greedy) }

  def extractSequence: FastaStep[Option[(String, String)]] = StateT.inspect { _.extractEntry }

  def entry(greedy: Boolean = false): FastaStep[Option[(String, String)]] = for {
    _ <- header
    _ <- sequence(greedy)
    maybeEntry <- extractSequence
  } yield maybeEntry

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    private val state = State(new mutable.StringBuilder(1000))

    setHandler(in, new InHandler {

      override def onPush(): Unit = {
        val line = grab(in).decodeString(StandardCharsets.UTF_8)

        state.appendLine(line)

        entry().runA(state) match {
          case Success(Some((h, s))) => push(out, FastaEntry(Id(h), Seq(s)))
          case Success(None)         => pull(in)
          case Failure(e)            => throw e
        }
      }

      override def onUpstreamFinish(): Unit = {
        entry(true).runA(state) match {
          case Success(Some((h, s))) => push(out, FastaEntry(Id(h), Seq(s)))
          case Success(None)         => pull(in)
          case Failure(e)            => throw e
        }
        super.onUpstreamFinish()
      }
    })

    setHandler(out, new OutHandler {
      override def onPull(): Unit =
        pull(in)
    })

  }

  override def shape: FlowShape[ByteString, FastaEntry] = FlowShape(in, out)
}
