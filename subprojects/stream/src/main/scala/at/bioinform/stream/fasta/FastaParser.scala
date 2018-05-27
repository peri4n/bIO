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

  val in: Inlet[ByteString] = Inlet[ByteString]("input")

  val out: Outlet[FastaEntry] = Outlet[FastaEntry]("output")

  val FastaHeaderStart = ">"

  val header: StateT[Try, mutable.StringBuilder, Header] = StateT(input =>
    if (!input.startsWith(FastaHeaderStart)) {
      Failure(FastaParserException(s"Expected a '$FastaHeaderStart' at the start of a fasta entry but read: '${input.head}'"))
    } else {
      val (id, rest) = input.tail.span(_ != '\n')
      Success((rest.filterNot(_ == '\n'), id.toString()))
    })

  val sequence: StateT[Try, mutable.StringBuilder, Option[String]] = StateT(input => {
    if (input.prefixLength(_ != '>') != input.length) { // new fasta header
      val size = input.prefixLength(_ != '>')
      val (sequence, rest) = input.splitAt(size)
      Success((rest + '\n', Some(sequence.toString())))
    } else {
      Success((input, None))
    }
  })

  val lastSequence: StateT[Try, mutable.StringBuilder, Option[String]] = StateT(input => {
      Try((input, Some(input.toString)))
  })

  val entry = for {
    header <- header
    body <- sequence
  } yield (header, body)

  val lastEntry = for {
    header <- header
    body <- lastSequence
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
          lastEntry.run(sequenceBuilder) match {
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
