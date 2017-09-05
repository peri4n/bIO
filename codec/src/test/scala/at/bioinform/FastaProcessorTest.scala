package at.bioinform

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, FunSpecLike}

class FastaProcessorTest extends TestKit(ActorSystem("FastaProcessorTest")) with FunSpecLike with BeforeAndAfterAll {

  implicit val materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  describe("A FASTA processor") {
    it("should parse the dna1 file.") {
      FastaProcessor.from(Paths.get(getClass.getResource("/fasta/dna1.fasta").toURI))
        .runWith(TestSink.probe[FastaEntry])
        .request(2)
        .expectNext(
          FastaEntry(">Test1",
            """AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC
              |TTCTGAACTGGTTACCTGCCGTGAGTAAATTAAAATTTTATTGACTTAGGTCACTAAATACTTTAACCAA
              |TATAGGCATAGCGCACAGACAGATAAAAATTACAGAGTACACAACATCCATGAAACGCATTAGCACCACC
              |ATTACCACCACCATCACCATTACCACAGGTAACGGTGCGGGCTGACGCGTACAGGAAACACAGAAAAAAG""".stripMargin.filter(_.isLetter)),
          FastaEntry(">Test2",
            """CCCGCACCTGACAGTGCGGGCTTTTTTTTTCGACCAAAGGTAACGAGGTAACAACCATGCGAGTGTTGAA
              |GTTCGGCGGTACATCAGTGGCAAATGCAGAACGTTTTCTGCGTGTTGCCGATATTCTGGAAAGCAATGCC
              |AGGCAGGGGCAGGTGGCCACCGTCCTCTCTGCCCCCGCCAAAATCACCAACCACCTGGTGGCGATGATTG
              |AAAAAACCATTAGCGGCCAGGATGCTTTACCCAATATCAGCGATGCCGAACGTATTTTTGCCGAACTTTT""".stripMargin.filter(_.isLetter)))
        .expectComplete()
    }
  }
}
