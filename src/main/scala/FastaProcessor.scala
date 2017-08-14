import java.nio.charset.StandardCharsets

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.util.ByteString

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/**
 * A flow that can parse Fasta formated files.
 */
class FastaProcessor extends GraphStage[FlowShape[ByteString, FastaEntry]] {
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    private var chunkAccumulator = StringBuilder.newBuilder

    private val processedEntries = new mutable.Queue[FastaEntry]()

    def parseHeader(chunk: String): Try[(String, String)] =
      if (!chunk.startsWith(">")) {
        Failure(new RuntimeException(s"Expected a '>' at the start of a fasta entry but read: '${chunk.head}'"))
      } else {
        Success(chunk.span(_ != '\n'))
      }

    def parseSequence(rest: String): Try[String] = Try(rest.filter(_ != '\n'))

    def extractEntry(chunk: String): Try[FastaEntry] =
      for {
        (header, rest) <- parseHeader(chunk)
        sequence <- parseSequence(rest)
      } yield FastaEntry(header, sequence)

    def chopOfEntry( builder: StringBuilder): (String, mutable.StringBuilder) = {
      val startOfNextEntry = builder.tail.indexWhere(_ == '>') + 1
      val (seq, rest) = builder.splitAt(startOfNextEntry)
      (seq.result(), rest)
    }

    private def containsCompleteEntries( accum: mutable.StringBuilder): Boolean = accum.count(_ == '>') > 1


    setHandler(in, new InHandler {
      override def onPush(): Unit = {
        chunkAccumulator ++= grab(in).decodeString(StandardCharsets.UTF_8)
        while (containsCompleteEntries(chunkAccumulator)) {
          val (seq, rest) = chopOfEntry(chunkAccumulator)
          chunkAccumulator = rest
          val entry = extractEntry(seq)
          if(entry.isSuccess) {
            processedEntries.enqueue(entry.get)
          } else {
            // fail silently: muhahahahahah evil laugh
          }
        }
        push(out, processedEntries.dequeue)
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

  val in = Inlet[ByteString]("input")
  val out = Outlet[FastaEntry]("output")

  override def shape: FlowShape[ByteString, FastaEntry] = FlowShape(in, out)
}
