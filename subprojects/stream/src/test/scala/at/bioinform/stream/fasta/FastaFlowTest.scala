package at.bioinform.stream.fasta

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import at.bioinform.lucene.segment.Segment
import at.bioinform.lucene.{Desc, Id, Seq}
import at.bioinform.stream.util.Splitter
import org.scalatest.{BeforeAndAfterAll, FunSpecLike}

class FastaFlowTest extends TestKit(ActorSystem("FastaProcessorTest")) with FunSpecLike with BeforeAndAfterAll {

  implicit val materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  describe("A FastaFlow") {
    it("should parse an empty file.") {
      FastaFlow.from(getClass.getResource("/at/bioinform/stream/fasta/fasta_empty.fa").toURI, Splitter.noop)
        .runWith(TestSink.probe[Segment])
        .request(10)
        .expectComplete()
    }

    it("should parse a very standard FASTA file.") {
      FastaFlow.from(getClass.getResource("/at/bioinform/stream/fasta/fasta_easy.fa").toURI, Splitter.noop)
        .runWith(TestSink.probe[Segment])
        .request(2)
        .expectNext(
          Segment(
            Id("Test1"),
            Seq(
              """AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC
                |TTCTGAACTGGTTACCTGCCGTGAGTAAATTAAAATTTTATTGACTTAGGTCACTAAATACTTTAACCAA
                |TATAGGCATAGCGCACAGACAGATAAAAATTACAGAGTACACAACATCCATGAAACGCATTAGCACCACC
                |ATTACCACCACCATCACCATTACCACAGGTAACGGTGCGGGCTGACGCGTACAGGAAACACAGAAAAAAG""".stripMargin.filter(_.isLetter))),
          Segment(
            Id("Test2"),
            Seq(
              """CCCGCACCTGACAGTGCGGGCTTTTTTTTTCGACCAAAGGTAACGAGGTAACAACCATGCGAGTGTTGAA
                |GTTCGGCGGTACATCAGTGGCAAATGCAGAACGTTTTCTGCGTGTTGCCGATATTCTGGAAAGCAATGCC
                |AGGCAGGGGCAGGTGGCCACCGTCCTCTCTGCCCCCGCCAAAATCACCAACCACCTGGTGGCGATGATTG
                |AAAAAACCATTAGCGGCCAGGATGCTTTACCCAATATCAGCGATGCCGAACGTATTTTTGCCGAACTTTT""".stripMargin.filter(_.isLetter))))
        .expectComplete()
    }

    it("should parse a FASTA file with empty lines.") {
      FastaFlow.from(getClass.getResource("/at/bioinform/stream/fasta/fasta_with_empty_lines.fa").toURI, Splitter.noop)
        .runWith(TestSink.probe[Segment])
        .request(2)
        .expectNext(
          Segment(
            Id("Test1"),
            Seq(
              """AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC
                |TTCTGAACTGGTTACCTGCCGTGAGTAAATTAAAATTTTATTGACTTAGGTCACTAAATACTTTAACCAA""".stripMargin.filter(_.isLetter))),
          Segment(
            Id("Test2"),
            Seq(
              """CCCGCACCTGACAGTGCGGGCTTTTTTTTTCGACCAAAGGTAACGAGGTAACAACCATGCGAGTGTTGAA
                |GTTCGGCGGTACATCAGTGGCAAATGCAGAACGTTTTCTGCGTGTTGCCGATATTCTGGAAAGCAATGCC""".stripMargin.filter(_.isLetter))))
        .expectComplete()
    }

    it("should parse a FASTA file with comments.") {
      FastaFlow.from(getClass.getResource("/at/bioinform/stream/fasta/fasta_with_comments.fa").toURI, Splitter.noop)
        .runWith(TestSink.probe[Segment])
        .request(2)
        .expectNext(
          Segment(
            Id("Test1"),
            Seq(
              """AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC
                |TTCTGAACTGGTTACCTGCCGTGAGTAAATTAAAATTTTATTGACTTAGGTCACTAAATACTTTAACCAA""".stripMargin.filter(_.isLetter))),
          Segment(
            Id("Test2"),
            Seq(
              """CCCGCACCTGACAGTGCGGGCTTTTTTTTTCGACCAAAGGTAACGAGGTAACAACCATGCGAGTGTTGAA
                |GTTCGGCGGTACATCAGTGGCAAATGCAGAACGTTTTCTGCGTGTTGCCGATATTCTGGAAAGCAATGCC""".stripMargin.filter(_.isLetter))))
        .expectComplete()
    }

    it("should parse a FASTA file containing sequence descriptions.") {
      FastaFlow.from(getClass.getResource("/at/bioinform/stream/fasta/fasta_with_descriptions.fa").toURI, Splitter.noop)
        .runWith(TestSink.probe[Segment])
        .request(2)
        .expectNext(
          Segment(
            Id("At1g02580"),
            Seq(
              """aggcgagtggttaatggagaaggaaaaccatgaggacgatggtgagggtttgccacccgaactaaatcagataaaa
                |gagcaaatcgaaaaggagagattctgcat""".stripMargin.filter(_.isLetter)),
            Some(Desc("mRNA (2291 bp) UTR's and CDS"))),
          Segment(
            Id("At1g65300:"),
            Seq(
              """atgaagagaaagatgaagttatcgttaatagaaaacagtgtatcgaggaaaacaacattcaccaaaaggaagaaag
                |ggatgacgaagaaactaaccgagctagtcactctatgtggtgttgaagcatgtgcggtcgtctatagtccgttcaa
                |gaccggaccaagaagatggtggatcaagagacttttataagtcaaaggatcg""".stripMargin.filter(_.isLetter)),
            Some(Desc("mRNA 837bp"))))
        .expectComplete()
    }
  }
}
