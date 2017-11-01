package at.bioinform.stream.lucene

import java.nio.file.Files

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import at.bioinform.stream.fasta.{FastaFlow, FastaSegmenter}
import org.apache.lucene.index.{DirectoryReader, Term}
import org.apache.lucene.search.{IndexSearcher, TermQuery}
import org.apache.lucene.store.MMapDirectory
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
      val path = Files.createTempDirectory("test")
      val index = new MMapDirectory(path)

      val future = FastaFlow.from(getClass.getResource("/at/bioinform/stream/lucene/fasta_easy.fa").toURI)
        .via(FastaSegmenter(10, 2))
        .via(DocumentFlow())
        .runWith(LuceneSink(index))

      val result = Await.result(future, 2 seconds)
      result.count should be(2)
      result.status.isSuccess should be(true)

      val searcher = new IndexSearcher(DirectoryReader.open(new MMapDirectory(path)))

      val t1 = new Term("sequence", "AGCTTT")
      val seqQuery = new TermQuery(t1)
      val seqTopDocs = searcher.search(seqQuery, 10)
      seqTopDocs.scoreDocs.map(_.doc) should contain theSameElementsInOrderAs Array(1, 0)
      seqTopDocs.totalHits should be(2)

      val t2 = new Term("id", "Test")
      val idQuery = new TermQuery(t2)
      val idTopDocs = searcher.search(idQuery, 10)
      idTopDocs.totalHits should be(2)
    }
  }

}
