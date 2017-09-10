package at.bioinform

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import akka.stream._
import akka.stream.scaladsl.{FileIO, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object FastaProcessor extends GraphStage[FlowShape[ByteString, FastaEntry]] {

  def from(path: Path): Source[FastaEntry, Future[IOResult]] = {
    FileIO.fromPath(path).via(FastaProcessor)
  }

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    private var chunkAccumulator = StringBuilder.newBuilder

    private val processedEntries = new mutable.Queue[FastaEntry]()

    def parseHeader(chunk: String): Try[(String, String)] =
      if (!chunk.startsWith(">")) {
        Failure(new RuntimeException(s"Expected a '>' at the start of a fasta entry but read: '${chunk.head}'"))
      } else {
        Success(chunk.span(_ != '\n'))
      }

    private def parseSequence(rest: String): Try[String] = Try(rest.filter(_ != '\n'))

    private def extractEntry(chunk: String): Try[FastaEntry] =
      for {
        (header, rest) <- parseHeader(chunk)
        sequence <- parseSequence(rest)
      } yield FastaEntry(header, sequence)

    private def chopOfEntry(builder: StringBuilder): (String, mutable.StringBuilder) = {
      val startOfNextEntry = builder.tail.indexWhere(_ == '>') + 1
      val (seq, rest) = builder.splitAt(startOfNextEntry)
      (seq.result(), rest)
    }

    private def containsCompleteEntries(accum: mutable.StringBuilder): Boolean = accum.count(_ == '>') > 1

    setHandler(in, new InHandler {
      override def onPush(): Unit = {
        chunkAccumulator ++= grab(in).decodeString(StandardCharsets.UTF_8)
        if (containsCompleteEntries(chunkAccumulator)) {
          while (containsCompleteEntries(chunkAccumulator)) {
            val (seq, rest) = chopOfEntry(chunkAccumulator)
            chunkAccumulator = rest
            val entry = extractEntry(seq)
            if (entry.isSuccess) {
              processedEntries.enqueue(entry.get)
            } else {
              // fail silently: muhahahahahah evil laugh
            }
          }
          push(out, processedEntries.dequeue)
        } else {
          pull(in)
        }
      }

      override def onUpstreamFinish(): Unit = {
        val (_, seq) = chopOfEntry(chunkAccumulator)
        push(out, extractEntry(seq.result()).get)
        super.onUpstreamFinish()
      }
    })

    setHandler(out, new OutHandler {
      override def onPull(): Unit =
        if (processedEntries.isEmpty) {
          pull(in)
        } else {
          push(out, processedEntries.dequeue)
        }
    })

  }

  val in: Inlet[ByteString] = Inlet[ByteString]("input")
  val out: Outlet[FastaEntry] = Outlet[FastaEntry]("output")

  override def shape: FlowShape[ByteString, FastaEntry] = FlowShape(in, out)
}
