package at.bioinform.codec

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, FunSpecLike}

class FastaFlowTest extends TestKit(ActorSystem("FastaProcessorTest")) with FunSpecLike with BeforeAndAfterAll {

  implicit val materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  describe("A FastaFlow") {
    it("should parse an empty file.") {
      FastaFlow.from(Paths.get(getClass.getResource("/fasta/fasta_empty.fa").toURI))
        .runWith(TestSink.probe[FastaEntry])
        .request(10)
        .expectComplete()
    }

    it("should parse a very standard FASTA file.") {
      FastaFlow.from(Paths.get(getClass.getResource("/fasta/fasta_easy.fa").toURI))
        .runWith(TestSink.probe[FastaEntry])
        .request(2)
        .expectNext(
          FastaEntry(
            "Test1",
            """AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC
              |TTCTGAACTGGTTACCTGCCGTGAGTAAATTAAAATTTTATTGACTTAGGTCACTAAATACTTTAACCAA
              |TATAGGCATAGCGCACAGACAGATAAAAATTACAGAGTACACAACATCCATGAAACGCATTAGCACCACC
              |ATTACCACCACCATCACCATTACCACAGGTAACGGTGCGGGCTGACGCGTACAGGAAACACAGAAAAAAG""".stripMargin.filter(_.isLetter)),
          FastaEntry(
            "Test2",
            """CCCGCACCTGACAGTGCGGGCTTTTTTTTTCGACCAAAGGTAACGAGGTAACAACCATGCGAGTGTTGAA
              |GTTCGGCGGTACATCAGTGGCAAATGCAGAACGTTTTCTGCGTGTTGCCGATATTCTGGAAAGCAATGCC
              |AGGCAGGGGCAGGTGGCCACCGTCCTCTCTGCCCCCGCCAAAATCACCAACCACCTGGTGGCGATGATTG
              |AAAAAACCATTAGCGGCCAGGATGCTTTACCCAATATCAGCGATGCCGAACGTATTTTTGCCGAACTTTT""".stripMargin.filter(_.isLetter)))
        .expectComplete()
    }

    it("should parse a FASTA file with empty lines.") {
      FastaFlow.from(Paths.get(getClass.getResource("/fasta/fasta_with_empty_lines.fa").toURI))
        .runWith(TestSink.probe[FastaEntry])
        .request(2)
        .expectNext(
          FastaEntry(
            "Test1",
            """AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC
              |TTCTGAACTGGTTACCTGCCGTGAGTAAATTAAAATTTTATTGACTTAGGTCACTAAATACTTTAACCAA""".stripMargin.filter(_.isLetter)),
          FastaEntry(
            "Test2",
            """CCCGCACCTGACAGTGCGGGCTTTTTTTTTCGACCAAAGGTAACGAGGTAACAACCATGCGAGTGTTGAA
              |GTTCGGCGGTACATCAGTGGCAAATGCAGAACGTTTTCTGCGTGTTGCCGATATTCTGGAAAGCAATGCC""".stripMargin.filter(_.isLetter)))
        .expectComplete()
    }

    it("should parse a FASTA file containing sequence descriptions.") {
      FastaFlow.from(Paths.get(getClass.getResource("/fasta/fasta_with_descriptions.fa").toURI))
        .runWith(TestSink.probe[FastaEntry])
        .request(2)
        .expectNext(
          FastaEntry(
            "At1g02580",
            Some("mRNA (2291 bp) UTR's and CDS"),
            """aggcgagtggttaatggagaaggaaaaccatgaggacgatggtgagggtttgccacccgaactaaatcagataaaa
              |gagcaaatcgaaaaggagagattctgcat""".stripMargin.filter(_.isLetter)),
          FastaEntry(
            "At1g65300:",
            Some("mRNA 837bp"),
            """atgaagagaaagatgaagttatcgttaatagaaaacagtgtatcgaggaaaacaacattcaccaaaaggaagaaag
              |ggatgacgaagaaactaaccgagctagtcactctatgtggtgttgaagcatgtgcggtcgtctatagtccgttcaa
              |gaccggaccaagaagatggtggatcaagagacttttataagtcaaaggatcg""".stripMargin.filter(_.isLetter)))
        .expectComplete()
    }
  }
}
