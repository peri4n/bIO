package at.bioinform.stream.fasta

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

  type Header = String

  type FastaStep[A] = StateT[Try, mutable.StringBuilder, A]

  val in: Inlet[ByteString] = Inlet[ByteString]("input")

  val out: Outlet[FastaEntry] = Outlet[FastaEntry]("output")

  val FastaHeaderStart = ">"

  val header: FastaStep[Header] = StateT {
    case input if input.isEmpty => Success((input, ""))
    case input if !isHeaderLine(input) =>
      Failure(FastaParserException(s"Expected a '$FastaHeaderStart' at the start of a fasta entry but read: '${input.head}'"))
    case input =>
      val (id, rest) = input.tail.span(_ != '\n')
      Success((rest.filterNot(_ == '\n'), id.toString()))
  }

  private def isHeaderLine(input: StringBuilder): Boolean = {
    input.startsWith(FastaHeaderStart)
  }

  /**
    * Parsing step to extract the entire sequence of an input.
    *
    * The returned sequence is represented as an optional string because
    * the sequence is only extracted completely. If no new Fasta entry is
    * seen, we can be sure that the sequence is complete.
    */
  val sequence: FastaStep[Option[String]] = StateT(input => {
    if (containsEntryStart(input)) {
      val size = input.prefixLength(_ != '>')
      val (sequence, rest) = input.splitAt(size)
      Success((rest + '\n', Some(sequence.toString())))
    } else {
      Success((input, None))
    }
  })

  private def containsEntryStart(input: StringBuilder): Boolean = {
    input.prefixLength(_ != '>') != input.length
  }

  val greedySequence: FastaStep[Option[String]] = StateT(input => {
      Try((StringBuilder.newBuilder, Some(input.toString)))
  })

  val entry: FastaStep[(Header, Option[String])] = for {
    header <- header
    body <- sequence
  } yield (header, body)

  val greedyEntry: FastaStep[(Header, Option[String])] = for {
    header <- header
    body <- greedySequence
  } yield (header, body)


  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    private var sequenceBuilder = new mutable.StringBuilder(1000)

    setHandler(in, new InHandler {

      override def onPush(): Unit = {
        val line = grab(in).decodeString(StandardCharsets.UTF_8)

        sequenceBuilder ++= (line + '\n')

        entry.run(sequenceBuilder) match {
          case Success((builder, (head, Some(body)))) => {
            push(out, FastaEntry(Id(head), Seq(body)))
            sequenceBuilder = builder
          }
          case Success((_, (_, None)))          => pull(in)
          case Failure(e)                       => throw e
        }
      }

      override def onUpstreamFinish(): Unit = {
          greedyEntry.run(sequenceBuilder) match {
            case Success((_, (head, Some(body)))) =>
              push(out, FastaEntry(Id(head), Seq(body)))
            case Success((_, (_, None)))          => throw FastaParserException("something went wrong")
            case Failure(e)                       => throw e
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
