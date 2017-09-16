package at.bioinform.codec.lucene

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Framing}
import akka.testkit.TestKit
import akka.util.ByteString
import at.bioinform.codec.fasta.FastaFlow
import org.apache.lucene.document.{Document, Field, TextField}
import org.apache.lucene.index.{DirectoryReader, Term}
import org.apache.lucene.search.{IndexSearcher, TermQuery}
import org.apache.lucene.store.RAMDirectory
import org.scalatest.{BeforeAndAfterAll, FunSpecLike, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class LuceneSinkTest extends TestKit(ActorSystem("FastaProcessorTest")) with FunSpecLike with Matchers with BeforeAndAfterAll {

  implicit val materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  describe("A LuceneSink") {
    it("should index FastaEntries") {
      val index = new RAMDirectory()

      val future = FileIO.fromPath(Paths.get(getClass.getResource("/lucene/fasta_easy.fa").toURI))
        .via(Framing.delimiter(ByteString(System.lineSeparator()), 200))
        .via(FastaFlow)
        .runWith(LuceneSink(index, entry => {
          val document = new Document()
          document.add(new Field("id", entry.header.id, TextField.TYPE_STORED))
          document.add(new Field("sequence", entry.sequence, TextField.TYPE_STORED))
          document
        }))

      val indexedSequences = Await.result(future, 2 seconds)
      indexedSequences should be(List("Test", "Test"))

      val searcher = new IndexSearcher(DirectoryReader.open(index))

      val t1 = new Term("sequence", "AGCT")
      val seqQuery = new TermQuery(t1)
      val seqTopDocs = searcher.search(seqQuery, 10)
      seqTopDocs.scoreDocs.map(_.doc) should contain theSameElementsInOrderAs Array(1, 0)
      seqTopDocs.totalHits should be(2)

      val t2 = new Term("id", "Test")
      val idQuery = new TermQuery(t2)
      val idTopDocs = searcher.search(idQuery, 10)
      idTopDocs.totalHits should be(2)

      index.close()
    }
  }

}
