package at.bioinform.seq

case class Seq(value: String) {

  def qGrams(q: Int, step: Int = 1): Iterable[Seq] = q match {
    case 0 => List(Seq(""))
    case _ => Iterable.range(0, value.length - q + 1, step).map { i =>
      Seq(value.substring(i, i + q))
    }
  }

  def qGrams(qs: Iterable[Int], step: Int = 1): Iterable[Seq] = {
    qs.flatMap(q => qGrams(q, step))
  }

  def qGrams(qMin: Int, qMax: Int, step: Int = 1): Iterable[Seq] = qGrams(qMin to qMax)

}

object Seq {

  implicit def string2Seq(x: String): Seq = Seq(x)

}
