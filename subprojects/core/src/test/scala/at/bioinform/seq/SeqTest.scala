package at.bioinform.seq

import at.bioinform.core.DNA4._
import at.bioinform.core._
import at.bioinform.generators.Generate
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

class SeqTest extends FunSpec with Matchers with GeneratorDrivenPropertyChecks {

  describe("A Seq") {

    it("can be constructed with an implicit alphabet (e.g DNA4).") {
      val value = Seq("ACGT")
      value.alphabet shouldBe DNA4
    }

    it("should have the correct bit pattern.") {
      Seq.fromString("A").bitPattern shouldBe "00"
      Seq.fromString("C").bitPattern shouldBe "01"
      Seq.fromString("G").bitPattern shouldBe "10"
      Seq.fromString("T").bitPattern shouldBe "11"
      Seq.fromString("TA").bitPattern shouldBe "0011"
      Seq.fromString("TC").bitPattern shouldBe "0111"
      Seq.fromString("TG").bitPattern shouldBe "1011"
      Seq.fromString("TT").bitPattern shouldBe "1111"
      Seq.fromString("CTA").bitPattern shouldBe "001101"
      Seq.fromString("CTC").bitPattern shouldBe "011101"
      Seq.fromString("CTG").bitPattern shouldBe "101101"
      Seq.fromString("CTT").bitPattern shouldBe "111101"
    }

    it("can be randomly accessed.") {
      val value = Seq("ACGT")
      value(0) shouldBe A
      value(1) shouldBe C
      value(2) shouldBe G
      value(3) shouldBe T
    }

    it("can compute substrings.") {
      forAll ((Generate.dna4WithPosition, "sequenceWithPosition")) { case (seq: String, pos: Int) =>
        println(seq)
        Seq(seq).substring(pos) shouldBe Seq(seq.substring(pos))
      }
    }

    it("can compare to itself.") {
      forAll ((Generate.dna4, "sequence1"), (Generate.dna4, "sequence2")) { (seq1: String, seq2: String) =>
        whenever(seq1 != seq2) {
          Seq.fromDna4(seq1) shouldNot be(Seq.fromDna4(seq2))
        }
      }
    }

    it("has the correct q grams.") {
      dna4"ACGTC".qGrams(0) should contain theSameElementsAs List("").map(Seq.fromDna4)
      dna4"ACGTC".qGrams(1) should contain theSameElementsAs List("A", "C", "G", "T", "C").map(Seq.fromDna4)
      dna4"ACGTC".qGrams(2) should contain theSameElementsAs List("AC", "CG", "GT", "TC").map(Seq.fromDna4)
      dna4"ACGTC".qGrams(3) should contain theSameElementsAs List("ACG", "CGT", "GTC").map(Seq.fromDna4)
    }

    it("has the correct q grams with step size 2.") {
      dna4"ACGTC".qGrams(0, 2) should contain theSameElementsAs List("").map(Seq.fromDna4)
      dna4"ACGTC".qGrams(1, 2) should contain theSameElementsAs List("A", "G", "C").map(Seq.fromDna4)
      dna4"ACGTC".qGrams(2, 2) should contain theSameElementsAs List("AC", "GT").map(Seq.fromDna4)
      dna4"ACGTC".qGrams(3, 2) should contain theSameElementsAs List("ACG", "GTC").map(Seq.fromDna4)
    }
  }

}
