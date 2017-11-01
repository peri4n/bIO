package at.bioinform.stream.fasta

import akka.stream.scaladsl.Flow
import at.bioinform.lucene.segment.Segment

object FastaSegmenter {

  def apply(size: Int, overlap: Int = 0) = Flow[FastaEntry].mapConcat { fastaEntry =>
    val lastStart = (fastaEntry.sequence.length().value / size) * size
    Range.inclusive(0, lastStart, size - overlap).map { start =>
      val seq = fastaEntry.sequence
      val end = math.min(start + size, seq.length().value)
      Segment(fastaEntry.id, seq.substring(start, end), fastaEntry.desc)
    }
  }
}
