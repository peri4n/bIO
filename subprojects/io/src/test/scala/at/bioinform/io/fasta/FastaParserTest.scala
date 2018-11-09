package at.bioinform.io.fasta

import cats.instances.try_._
import org.scalacheck.Shrink
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FunSpec, Matchers}

import scala.util.{Failure, Success}

class FastaParserTest extends FunSpec with Matchers with PropertyChecks {

  implicit def noShrink[T]: Shrink[T] = Shrink.shrinkAny

  describe("A Fasta parser") {
    it("should extract the header line of a string builder in correct state") {
      val input = FastaParser.State(new StringBuilder(20))

      FastaParser.add(">id1 desc").runS(input) match {
        case Success(newState) =>
          newState.headerEnd shouldBe 9
          newState.buffer.toString() shouldBe ">id1 desc"
        case _ => fail()
      }
    }

    it("should fail if the string builder doesn't start with a '>'") {
      val input = FastaParser.State(new StringBuilder(20))

      FastaParser.add("id1").runS(input) match {
        case Failure(exception) =>
          exception shouldBe a [FastaParserException]
        case _ => fail()
      }
    }

    it("should not accept an empty string") {
      val input = FastaParser.State(new StringBuilder(20))


      FastaParser.add("").runA(input) match {
        case Failure(exception) =>
          exception shouldBe a [FastaParserException]
        case _ => fail()
      }
    }

    it("should parse all ") {
      forAll { (header: String, line1: String, line2: String) =>
        whenever(!header.isEmpty) {
          val input = FastaParser.State(new StringBuilder(20))

          val Success((state1, value1)) = FastaParser.add(">" + header).run(input)
          value1 match {
            case None =>
            case _ => fail()
          }

          val Success((state2, value2)) = FastaParser.add(line1).run(state1)
          value2 match {
            case None =>
            case _ => fail()
          }

          val Success((_, value3)) = FastaParser.add(line2, true).run(state2)
          value3 match {
            case Some(entry) =>
              entry.id.value shouldBe header
              entry.sequence.value shouldBe (line1 + line2)
            case _ => fail()
          }
        }
      }
    }
  }
}
