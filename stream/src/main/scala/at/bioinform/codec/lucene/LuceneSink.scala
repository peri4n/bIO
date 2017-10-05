package at.bioinform.codec.lucene

import akka.stream.stage.{GraphStageLogic, GraphStageWithMaterializedValue, InHandler}
import akka.stream.{Attributes, Inlet, SinkShape}
import at.bioinform.codec.fasta.FastaEntry
import at.bioinform.lucene.Util
import org.apache.lucene.document.Document
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.Directory

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}

/**
  * A sink that stores all incoming [[FastaEntry]] inside a Lucene [[Directory]].
  *
  * The provided directory is closed after the stream is run.
  *
  * @param directory Lucene index where the FASTA entries should be stored.
  * @param transformer Converts FASTA entries to documents
  */
case class LuceneSink(directory: Directory, transformer: FastaEntry => Document) extends GraphStageWithMaterializedValue[SinkShape[FastaEntry], Future[List[String]]] {

  val in: Inlet[FastaEntry] = Inlet("input")

  override def shape = SinkShape(in)

  override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, Future[List[String]]) = {

    val promise = Promise[List[String]]()

    val logic = new GraphStageLogic(shape) {

      private var indexedIds = ListBuffer.empty[String]

      private val writer = new IndexWriter(directory, new IndexWriterConfig(Util.analyzer(6, 6)))

      override def preStart(): Unit = {
        pull(in)
      }

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val entry = grab(in)
          val document = transformer(entry)
          writer.addDocument(document)
          indexedIds += entry.header.id
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
