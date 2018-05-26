package at.bioinform.stream.lucene

import akka.Done
import akka.stream.stage.{GraphStageLogic, GraphStageWithMaterializedValue, InHandler}
import akka.stream.{Attributes, IOResult, Inlet, SinkShape}
import at.bioinform.lucene.Analyzers
import org.apache.lucene.document.Document
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.Directory

import scala.concurrent.{Future, Promise}
import scala.util.Try

/**
  * A sink that stores all incoming [[at.bioinform.lucene.segment.Segment]] inside a Lucene [[Directory]].
  *
  * The provided directory is closed after the stream is run.
  *
  * @param directory Lucene index where the segments should be stored.
  */
case class LuceneSink(directory: Directory) extends GraphStageWithMaterializedValue[SinkShape[Document], Future[IOResult]] {

  val in: Inlet[Document] = Inlet("input")

  override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, Future[IOResult]) = {

    val promise = Promise[IOResult]()

    val logic = new GraphStageLogic(shape) {

      /** Number of indexed sequences. */
      private var sequenceCount = 0

      private val writer = new IndexWriter(directory, new IndexWriterConfig(Analyzers.ngram(6, 6)))

      override def preStart(): Unit = {
        pull(in)
      }

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val document = grab(in)
          writer.addDocument(document)
          sequenceCount += 1
          pull(in)
        }

        override def onUpstreamFinish(): Unit = {
          writer.commit()
          directory.close()
          promise.trySuccess(IOResult(sequenceCount, Try(Done)))
          super.onUpstreamFinish()
        }
      })
    }
    (logic, promise.future)
  }

  override def shape = SinkShape(in)
}
