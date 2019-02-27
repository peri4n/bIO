package at.bioinform.seq

import at.bioinform.core.alphabet.dna._
import at.bioinform.generators.Generate
import org.scalacheck.Shrink
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

class ChainOfDna5Test extends FunSpec with Matchers with GeneratorDrivenPropertyChecks {

  import at.bioinform.core.alphabet.dna.DNA5._

  implicit val noShrinkSeq: Shrink[String] = Shrink.shrinkAny

  implicit val noShrinkPair: Shrink[(String, Int)] = Shrink.shrinkAny

  describe("A chain") {

    it("can be constructed with an implicit alphabet (e.g DNA4).") {
      val value = Chain("ACGT")
      value.alphabet shouldBe DNA5
    }

    it("should have the correct bit pattern.") {
      Chain.fromString("A").bitPattern shouldBe "000"
      Chain.fromString("C").bitPattern shouldBe "001"
      Chain.fromString("G").bitPattern shouldBe "010"
      Chain.fromString("T").bitPattern shouldBe "011"
      Chain.fromString("N").bitPattern shouldBe "100"
      Chain.fromString("TA").bitPattern shouldBe "000011"
      Chain.fromString("TC").bitPattern shouldBe "001011"
      Chain.fromString("TG").bitPattern shouldBe "010011"
      Chain.fromString("TT").bitPattern shouldBe "011011"
      Chain.fromString("TN").bitPattern shouldBe "100011"
      Chain.fromString("CTA").bitPattern shouldBe "000011001"
      Chain.fromString("CTC").bitPattern shouldBe "001011001"
      Chain.fromString("CTG").bitPattern shouldBe "010011001"
      Chain.fromString("CTT").bitPattern shouldBe "011011001"
      Chain.fromString("CTN").bitPattern shouldBe "100011001"
    }

    it("can be randomly accessed.") {
      val value = Chain("ACGTN")
      value(0) shouldBe A
      value(1) shouldBe C
      value(2) shouldBe G
      value(3) shouldBe T
      value(4) shouldBe N
    }

    it("can compute substrings.") {
      forAll ((Generate.dna5WithLength, "sequenceWithPosition")) { case (seq: String, pos: Int) =>
        Chain(seq).substring(pos) shouldBe Chain(seq.substring(pos))
        Chain(seq).substring(pos).length shouldBe (seq.length - pos)
      }
    }

    it("can compare to itself.") {
      forAll ((Generate.dna5, "sequence1"), (Generate.dna5, "sequence2")) { (seq1: String, seq2: String) =>
        whenever(seq1 != seq2) {
          Chain.fromDna5(seq1) shouldNot be(Chain.fromDna5(seq2))
        }
      }
    }

    it("has the correct q grams.") {
      dna5"ACGTCN".qGrams(0) should contain theSameElementsAs List("").map(Chain.fromDna5)
      dna5"ACGTCN".qGrams(1) should contain theSameElementsAs List("A", "C", "G", "T", "C", "N").map(Chain.fromDna5)
      dna5"ACGTCN".qGrams(2) should contain theSameElementsAs List("AC", "CG", "GT", "TC", "CN").map(Chain.fromDna5)
      dna5"ACGTCN".qGrams(3) should contain theSameElementsAs List("ACG", "CGT", "GTC", "TCN").map(Chain.fromDna5)
    }

    it("has the correct q grams with step size 2.") {
      dna5"ACGTCAN".qGrams(0, 2) should contain theSameElementsAs List("").map(Chain.fromDna5)
      dna5"ACGTCAN".qGrams(1, 2) should contain theSameElementsAs List("A", "G", "C", "N").map(Chain.fromDna5)
      dna5"ACGTCAN".qGrams(2, 2) should contain theSameElementsAs List("AC", "GT", "CA").map(Chain.fromDna5)
      dna5"ACGTCAN".qGrams(3, 2) should contain theSameElementsAs List("ACG", "GTC", "CAN").map(Chain.fromDna5)
    }

    it("can be converted to string.") {
      forAll ((Generate.dna5, "sequence")) { seq: String =>
        Chain.fromString(seq).toString shouldBe seq
      }
    }

    it("can be converted to string with unknown characters converted to A") {
      dna5"ACxTC".toString shouldBe "ACATC"
    }
  }

}
