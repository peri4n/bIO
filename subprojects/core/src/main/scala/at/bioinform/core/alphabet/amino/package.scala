package at.bioinform.core.alphabet

package object amino {

  sealed trait AA20 extends Symbol {

    def tripletCode: String

    def name: String

  }

  case object A extends AA20 {
    val char = 'A'
    val tripletCode = "Ala"
    val name = "Alanine"
  }

  case object C extends AA20 {
    val char = 'C'
    val tripletCode = "Cys"
    val name = "Cysteine"
  }

  case object D extends AA20 {
    val char = 'D'
    val tripletCode = "Asp"
    val name = "Aspartic Acid"
  }

  case object E extends AA20 {
    val char = 'E'
    val tripletCode = "Glu"
    val name = "Glutamic Acid"
  }

  case object F extends AA20 {
    val char = 'F'
    val tripletCode = "Phe"
    val name = "Phenylalanine"
  }

  case object G extends AA20 {
    val char = 'G'
    val tripletCode = "Gly"
    val name = "Glycine"
  }

  case object H extends AA20 {
    val char = 'H'
    val tripletCode = "His"
    val name = "Histidine"
  }

  case object I extends AA20 {
    val char = 'I'
    val tripletCode = "Ile"
    val name = "Isoleucine"
  }

  case object K extends AA20 {
    val char = 'K'
    val tripletCode = "Lys"
    val name = "Lysine"
  }

  case object L extends AA20 {
    val char = 'L'
    val tripletCode = "Leu"
    val name = "Leucine"
  }

  case object M extends AA20 {
    val char = 'M'
    val tripletCode = "Met"
    val name = "Methionine"
  }

  case object N extends AA20 {
    val char = 'N'
    val tripletCode = "Asn"
    val name = "Asparagine"
  }

  case object P extends AA20 {
    val char = 'P'
    val tripletCode = "Pro"
    val name = "Proline"
  }

  case object Q extends AA20 {
    val char = 'Q'
    val tripletCode = "Gln"
    val name = "Glutamine"
  }

  case object R extends AA20 {
    val char = 'R'
    val tripletCode = "Arg"
    val name = "Arginine"
  }

  case object S extends AA20 {
    val char = 'S'
    val tripletCode = "Ser"
    val name = "Serine"
  }

  case object T extends AA20 {
    val char = 'T'
    val tripletCode = "Thr"
    val name = "Threonine"
  }

  case object V extends AA20 {
    val char = 'V'
    val tripletCode = "Val"
    val name = "Valine"
  }

  case object W extends AA20 {
    val char = 'W'
    val tripletCode = "Trp"
    val name = "Tryptophan"
  }

  case object Y extends AA20 {
    val char = 'Y'
    val tripletCode = "Tyr"
    val name = "Tyrosine"
  }

}
