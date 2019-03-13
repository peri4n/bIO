package at.bioinform.core.alphabet.dna

import at.bioinform.generators.Generate
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

class DNA4Test extends FunSpec with Matchers with GeneratorDrivenPropertyChecks {

  describe("DNA4") {

    it("have the correct elements") {
      DNA4.elements shouldBe List(A, C, G, T)
      DNA4.size shouldBe 4
    }

    it("can convert chars to integer") {
      DNA4.toInt('A') shouldBe 0
      DNA4.toInt('a') shouldBe 0
      DNA4.toInt('C') shouldBe 1
      DNA4.toInt('c') shouldBe 1
      DNA4.toInt('G') shouldBe 2
      DNA4.toInt('g') shouldBe 2
      DNA4.toInt('T') shouldBe 3
      DNA4.toInt('t') shouldBe 3

      forAll { nuc: Char =>
        whenever(!DNA4.elements.map(_.char).contains(nuc)) {
          DNA4.toInt(nuc) shouldBe 0
        }
      }
    }

    it("can convert symbols to integer") {
      DNA4.toInt(A) shouldBe 0
      DNA4.toInt(C) shouldBe 1
      DNA4.toInt(G) shouldBe 2
      DNA4.toInt(T) shouldBe 3
    }

    it("can convert from integers to symbols") {
      DNA4.fromInt(0) shouldBe A
      DNA4.fromInt(1) shouldBe C
      DNA4.fromInt(2) shouldBe G
      DNA4.fromInt(3) shouldBe T
      DNA4.fromInt(4) shouldBe A
    }

    it("toChar and fromChar should lead to identity (upper cased)") {
      forAll(Generate.nuc4Char) { char: Char =>
        DNA4.fromChar(char).char shouldBe char.toUpper
      }
    }

    it("toInt and fromInt should lead to identity") {
      forAll(Generate.nuc4Index) { number: Int =>
        DNA4.toInt(DNA4.fromInt(number)) shouldBe number
      }
    }
  }

}
