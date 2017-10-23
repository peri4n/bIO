package at.bioinform.stream.lucene

import akka.stream.stage.{GraphStageLogic, GraphStageWithMaterializedValue, InHandler}
import akka.stream.{Attributes, Inlet, SinkShape}
import at.bioinform.lucene.Analyzers
import at.bioinform.lucene.segment.Segment
import org.apache.lucene.document.Document
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.Directory

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}

/**
 * A sink that stores all incoming [[Segment]] inside a Lucene [[Directory]].
 *
 * The provided directory is closed after the stream is run.
 *
 * @param directory Lucene index where the segments should be stored.
 */
case class LuceneSink(directory: Directory) extends GraphStageWithMaterializedValue[SinkShape[Document], Future[List[String]]] {

  val in: Inlet[Document] = Inlet("input")

  override def shape = SinkShape(in)

  override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, Future[List[String]]) = {

    val promise = Promise[List[String]]()

    val logic = new GraphStageLogic(shape) {

      private val indexedIds = ListBuffer.empty[String]

      private val writer = new IndexWriter(directory, new IndexWriterConfig(Analyzers.ngram(6, 6)))

      override def preStart(): Unit = {
        pull(in)
      }

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val document = grab(in)
          writer.addDocument(document)
          pull(in)
        }

        override def onUpstreamFinish(): Unit = {
          super.onUpstreamFinish()
          writer.commit()
          directory.close()
          promise.trySuccess(indexedIds.result())
        }
      })
    }
    (logic, promise.future)
  }
}
