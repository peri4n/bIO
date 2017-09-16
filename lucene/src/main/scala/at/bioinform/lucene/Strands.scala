package at.bioinform.lucene

sealed trait Strands

object Strands {
  def from(str: String) = str.toLowerCase match {
    case "forward" => Forward
    case "reverse" => Reverse
    case "both"    => Both
    case _         => throw new IllegalArgumentException("Possible strands are [forward, reverse, both]")
  }
}

case object Forward extends Strands
case object Reverse extends Strands
case object Both extends Strands
