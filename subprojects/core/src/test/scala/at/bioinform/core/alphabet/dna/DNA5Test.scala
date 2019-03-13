package at.bioinform.core.alphabet.dna

import at.bioinform.generators.Generate
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

class DNA5Test extends FunSpec with Matchers with GeneratorDrivenPropertyChecks {

  describe("DNA5") {

    it("have the correct elements") {
      DNA5.elements shouldBe List(A, C, G, T, N)
      DNA5.size shouldBe 5
    }

    it("can convert chars to integer") {
      DNA5.toInt('A') shouldBe 0
      DNA5.toInt('a') shouldBe 0
      DNA5.toInt('C') shouldBe 1
      DNA5.toInt('c') shouldBe 1
      DNA5.toInt('G') shouldBe 2
      DNA5.toInt('g') shouldBe 2
      DNA5.toInt('T') shouldBe 3
      DNA5.toInt('t') shouldBe 3
      DNA5.toInt('N') shouldBe 4
      DNA5.toInt('n') shouldBe 4

      forAll { nuc: Char =>
        whenever(!DNA5.elements.map(_.char).contains(nuc)) {
          DNA4.toInt(nuc) shouldBe 0
        }
      }
    }

    it("can convert symbols to integer") {
      DNA5.toInt(A) shouldBe 0
      DNA5.toInt(C) shouldBe 1
      DNA5.toInt(G) shouldBe 2
      DNA5.toInt(T) shouldBe 3
      DNA5.toInt(N) shouldBe 4
    }

    it("can convert from integers to symbols") {
      DNA5.fromInt(0) shouldBe A
      DNA5.fromInt(1) shouldBe C
      DNA5.fromInt(2) shouldBe G
      DNA5.fromInt(3) shouldBe T
      DNA5.fromInt(4) shouldBe N
      DNA5.fromInt(5) shouldBe A
    }

    it("toChar and fromChar should lead to identity (upper cased)") {
      forAll(Generate.nuc4Char) { char: Char =>
        DNA5.fromChar(char).char shouldBe char.toUpper
      }
    }

    it("toInt and fromInt should lead to identity") {
      forAll(Generate.nuc5Index) { number: Int =>
        DNA5.toInt(DNA5.fromInt(number)) shouldBe number
      }
    }
  }

}
