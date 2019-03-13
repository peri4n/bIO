package at.bioinform.io.fasta

import java.nio.charset.StandardCharsets

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.util.ByteString
import at.bioinform.io._
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

    def appendLine(line: String): Try[State] = {
      if (startsWithHeader(line)) {
        if (headerEnd == -1) { // new header
          Success(copy(buffer = buffer.append(line), headerEnd = buffer.length))
        } else { // next header
          Success(copy(sequenceEnd = buffer.length, buffer = buffer.append(line)))
        }
      } else {
        if (headerEnd == -1) {
          Failure(FastaParserException("Expected a start of a header file"))
        } else {
          Success(copy(buffer = buffer.append(line)))
        }
      }
    }

    def startsWithHeader(line: String): Boolean = line.startsWith(FastaHeaderStart)

    def extractEntry(greedy: Boolean): Try[(State, Option[FastaEntry])] = {
      if (greedy) {
        val header = buffer.substring(1, headerEnd)
        val sequence = buffer.substring(headerEnd, buffer.length)
        buffer.clear()
        Success((copy(buffer = this.buffer), Some(FastaEntry(Id(header), Seq(sequence)))))
      } else {
        if (sequenceEnd == -1) {
          Success((this, None))
        } else {
          val header = buffer.substring(1, headerEnd)
          val sequence = buffer.substring(headerEnd, sequenceEnd)
          for (i <- sequenceEnd until buffer.length) {
            buffer.setCharAt(i - sequenceEnd, buffer.charAt(i))
          }
          buffer.setLength(buffer.length - sequenceEnd)
          Success((copy(sequenceEnd = -1, headerEnd = buffer.length, buffer = this.buffer), Some(FastaEntry(Id(header), Seq(sequence)))))
        }
      }
    }
  }

  val in: Inlet[ByteString] = Inlet[ByteString]("input")

  val out: Outlet[FastaEntry] = Outlet[FastaEntry]("output")

  val FastaHeaderStart = ">"

  private def appendLine(line: String): FastaStep[Unit] = StateT.modifyF {
    _.appendLine(line)
  }

  def entry(greedy: Boolean = false): FastaStep[Option[FastaEntry]] = StateT {
    _.extractEntry(greedy)
  }

  def add(line: String, greedy: Boolean = false): FastaStep[Option[FastaEntry]] = {
    for {
      _ <- appendLine(line)
      entry <- entry(greedy)
    } yield entry
  }

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    private var state = State(new mutable.StringBuilder(1000))

    setHandler(in, new InHandler {

      override def onPush(): Unit = {
        val line = grab(in).decodeString(StandardCharsets.UTF_8)

        val Success((newState, ret)) = add(line).run(state)
        ret match {
          case Some(entry) => push(out, entry)
          case None => pull(in)
        }

        state = newState
      }

      override def onUpstreamFinish(): Unit = {
        val Success((_, ret)) = add("", true).run(state)
        ret match {
          case Some(entry) => push(out, entry)
          case None => pull(in)
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
