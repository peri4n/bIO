package at.bioinform.core.alphabet.amino

import at.bioinform.core.alphabet.Alphabet

object Amino20 extends Alphabet {

  type elemType = AA20

  val elements = List(A, C, D, E, F, G, H, I, K, L, M, N, P, Q, R, S, T, V, W, Y)

  override val size: Int = 20

  override val isCaseSensitive: Boolean = false
}
