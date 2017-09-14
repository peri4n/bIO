package at.bioinform.codec.lucene

import akka.stream.stage.{GraphStageLogic, GraphStageWithMaterializedValue, InHandler}
import akka.stream.{Attributes, Inlet, SinkShape}
import at.bioinform.codec.fasta.FastaEntry
import org.apache.lucene.analysis.core.WhitespaceAnalyzer
import org.apache.lucene.analysis.ngram.NGramTokenizer
import org.apache.lucene.document.Document
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.Directory

import scala.concurrent.{Future, Promise}

case class LuceneSink(directory: Directory, transformer: FastaEntry => Document) extends GraphStageWithMaterializedValue[SinkShape[FastaEntry], Future[Int]] {

  val in: Inlet[FastaEntry] = Inlet("input")

  override def shape = SinkShape(in)

  override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, Future[Int]) = {

    val promise = Promise[Int]()

    val logic = new GraphStageLogic(shape) {

      private var indexedSequences = 0

      private val writer = new IndexWriter(directory, new IndexWriterConfig(Util.analyzer))

      override def preStart(): Unit = {
        pull(in)
      }

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val document = transformer(grab(in))
          writer.addDocument(document)
          indexedSequences += 1
          pull(in)
        }

        override def onUpstreamFinish(): Unit = {
          super.onUpstreamFinish()
          writer.commit()
          promise.trySuccess(indexedSequences)
        }
      })
    }
    (logic, promise.future)
  }
}
