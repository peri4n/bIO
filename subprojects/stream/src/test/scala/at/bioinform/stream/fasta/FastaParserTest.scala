package at.bioinform.stream.fasta

import cats.instances.try_._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FunSpec, Matchers}

import scala.util.{Failure, Success}

class FastaParserTest extends FunSpec with Matchers with PropertyChecks {

  describe("A Fasta parser") {
    it("should extract the header line of a string builder in correct state") {
      val input = new StringBuilder(20, ">id1 desc")

      FastaParser.header.run(input) match {
        case Success((builder, id)) =>
          id shouldBe "id1 desc"
          builder shouldBe empty
        case _ => fail()
      }
    }

    it("should fail if the string builder doesn't start with a '>'") {
      val input = new StringBuilder(20, "id1")

      FastaParser.header.run(input) match {
        case Failure(exception) =>
          exception shouldBe a [FastaParserException]
        case _ => fail()
      }
    }

    it("should accept an empty string") {
      val input = new StringBuilder(0, "")

      FastaParser.entry.runA(input) match {
        case Success((header, None)) =>
          header shouldBe empty
        case _ => fail()
      }
    }

    it("should parse all ") {
      forAll { (header: String, sequence: String) =>
        val input = new StringBuilder(20, s">$header\n$sequence")
        FastaParser.greedyEntry.run(input) match {
          case Success((builder, (h, Some(s)))) =>
            h shouldBe header
            s shouldBe sequence
            builder shouldBe empty
          case _ => fail()
        }
      }
    }
  }
}
