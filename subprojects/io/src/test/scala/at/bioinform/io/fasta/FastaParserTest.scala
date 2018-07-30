package at.bioinform.io.fasta

import cats.instances.try_._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FunSpec, Matchers}

import scala.util.{Failure, Success}

class FastaParserTest extends FunSpec with Matchers with PropertyChecks {

  describe("A Fasta parser") {
    it("should extract the header line of a string builder in correct state") {
      val buffer = new StringBuilder(20, ">id1 desc")
      val input = FastaParser.State(buffer)

      FastaParser.header.runS(input) match {
        case Success(newState) =>
          newState.headerEnd shouldBe 9
          newState.buffer shouldBe buffer
        case _ => fail()
      }
    }

    it("should fail if the string builder doesn't start with a '>'") {
      val input = FastaParser.State(new StringBuilder(20, "id1"))

      FastaParser.header.runS(input) match {
        case Failure(exception) =>
          exception shouldBe a [FastaParserException]
        case _ => fail()
      }
    }

    it("should not accept an empty string") {
      val input = FastaParser.State(new StringBuilder(0, ""))

      FastaParser.entry().runA(input) match {
        case Failure(exception) =>
          exception shouldBe a [FastaParserException]
        case _ => fail()
      }
    }

    it("should parse all ") {
      forAll { (header: String, sequence: String) =>
        whenever(!header.isEmpty && !sequence.isEmpty) {
          val input = FastaParser.State(new StringBuilder(20, s">$header"))

          FastaParser.entry(true).runA(input) match {
            case Success(Some((h, s))) =>
              h shouldBe header
              s shouldBe None
            case _                     => fail()
          }
        }
      }
    }
  }
}
