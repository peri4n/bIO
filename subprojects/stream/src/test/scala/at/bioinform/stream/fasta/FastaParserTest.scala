package at.bioinform.stream.fasta

import org.scalatest.{FunSpec, Matchers}

import scala.util.{Failure, Success}
import cats.instances.try_._

class FastaParserTest extends FunSpec with Matchers {

  describe("A Fasta parser") {
    it("should extract the header line of a string builder in correct state") {
      val input = new StringBuilder(20, ">id1 desc")

      FastaParser.header.run(input) match {
        case Failure(_) => fail()
        case Success((builder, id)) =>
          id shouldBe "id1 desc"
          builder shouldBe empty
      }
    }

    it("should fail if the string builder is in an illegal state") {
      val input = new StringBuilder(20, "id1")

      FastaParser.header.run(input) match {
        case Failure(exception) =>
          exception shouldBe a [FastaParserException]
        case Success(_) => fail()

      }

    }
  }
}
