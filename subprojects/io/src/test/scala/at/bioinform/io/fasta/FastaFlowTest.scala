package at.bioinform.io.fasta

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import at.bioinform.lucene.{Id, Seq}
import org.scalatest.{BeforeAndAfterAll, FunSpecLike}

class FastaFlowTest extends TestKit(ActorSystem("FastaProcessorTest")) with FunSpecLike with BeforeAndAfterAll {

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  describe("A FastaFlow") {
    ignore("should parse an empty file.") {
      FastaFlow.from(getClass.getResource("/at/bioinform/io/fasta/fasta_empty.fa").toURI)
        .runWith(TestSink.probe[FastaEntry])
        .request(10)
        .expectComplete()
    }

    it("should parse a very standard FASTA file.") {
      FastaFlow.from(getClass.getResource("/at/bioinform/io/fasta/fasta_easy.fa").toURI)
        .runWith(TestSink.probe[FastaEntry])
        .request(2)
        .expectNext(
          FastaEntry(
            Id("Test1"),
            Seq(
              """AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC
                |TTCTGAACTGGTTACCTGCCGTGAGTAAATTAAAATTTTATTGACTTAGGTCACTAAATACTTTAACCAA
                |TATAGGCATAGCGCACAGACAGATAAAAATTACAGAGTACACAACATCCATGAAACGCATTAGCACCACC
                |ATTACCACCACCATCACCATTACCACAGGTAACGGTGCGGGCTGACGCGTACAGGAAACACAGAAAAAAG""".stripMargin.filter(_.isLetter))),
          FastaEntry(
            Id("Test2"),
            Seq(
              """CCCGCACCTGACAGTGCGGGCTTTTTTTTTCGACCAAAGGTAACGAGGTAACAACCATGCGAGTGTTGAA
                |GTTCGGCGGTACATCAGTGGCAAATGCAGAACGTTTTCTGCGTGTTGCCGATATTCTGGAAAGCAATGCC
                |AGGCAGGGGCAGGTGGCCACCGTCCTCTCTGCCCCCGCCAAAATCACCAACCACCTGGTGGCGATGATTG
                |AAAAAACCATTAGCGGCCAGGATGCTTTACCCAATATCAGCGATGCCGAACGTATTTTTGCCGAACTTTT""".stripMargin.filter(_.isLetter))))
        .expectComplete()
    }

    it("should parse a FASTA file with empty lines.") {
      FastaFlow.from(getClass.getResource("/at/bioinform/io/fasta/fasta_with_empty_lines.fa").toURI)
        .runWith(TestSink.probe[FastaEntry])
        .request(2)
        .expectNext(
          FastaEntry(
            Id("Test1"),
            Seq(
              """AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC
                |TTCTGAACTGGTTACCTGCCGTGAGTAAATTAAAATTTTATTGACTTAGGTCACTAAATACTTTAACCAA""".stripMargin.filter(_.isLetter))),
          FastaEntry(
            Id("Test2"),
            Seq(
              """CCCGCACCTGACAGTGCGGGCTTTTTTTTTCGACCAAAGGTAACGAGGTAACAACCATGCGAGTGTTGAA
                |GTTCGGCGGTACATCAGTGGCAAATGCAGAACGTTTTCTGCGTGTTGCCGATATTCTGGAAAGCAATGCC""".stripMargin.filter(_.isLetter))))
        .expectComplete()
    }

    it("should parse a FASTA file with comments.") {
      FastaFlow.from(getClass.getResource("/at/bioinform/io/fasta/fasta_with_comments.fa").toURI)
        .runWith(TestSink.probe[FastaEntry])
        .request(2)
        .expectNext(
          FastaEntry(
            Id("Test1"),
            Seq(
              """AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC
                |TTCTGAACTGGTTACCTGCCGTGAGTAAATTAAAATTTTATTGACTTAGGTCACTAAATACTTTAACCAA""".stripMargin.filter(_.isLetter))),
          FastaEntry(
            Id("Test2"),
            Seq(
              """CCCGCACCTGACAGTGCGGGCTTTTTTTTTCGACCAAAGGTAACGAGGTAACAACCATGCGAGTGTTGAA
                |GTTCGGCGGTACATCAGTGGCAAATGCAGAACGTTTTCTGCGTGTTGCCGATATTCTGGAAAGCAATGCC""".stripMargin.filter(_.isLetter))))
        .expectComplete()
    }

    it("should parse a FASTA file containing sequence descriptions.") {
      FastaFlow.from(getClass.getResource("/at/bioinform/io/fasta/fasta_with_descriptions.fa").toURI)
        .runWith(TestSink.probe[FastaEntry])
        .request(2)
        .expectNext(
          FastaEntry(
            Id("At1g02580 mRNA (2291 bp) UTR's and CDS"),
            Seq(
              """aggcgagtggttaatggagaaggaaaaccatgaggacgatggtgagggtttgccacccgaactaaatcagataaaa
                |gagcaaatcgaaaaggagagattctgcat""".stripMargin.filter(_.isLetter))
          ),
          FastaEntry(
            Id("At1g65300: mRNA 837bp"),
            Seq(
              """atgaagagaaagatgaagttatcgttaatagaaaacagtgtatcgaggaaaacaacattcaccaaaaggaagaaag
                |ggatgacgaagaaactaaccgagctagtcactctatgtggtgttgaagcatgtgcggtcgtctatagtccgttcaa
                |gaccggaccaagaagatggtggatcaagagacttttataagtcaaaggatcg""".stripMargin.filter(_.isLetter))
          ))
        .expectComplete()
    }
  }
}
