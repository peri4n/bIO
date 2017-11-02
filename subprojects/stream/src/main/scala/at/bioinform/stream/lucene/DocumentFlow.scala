package at.bioinform.stream.lucene

import akka.stream.scaladsl.Flow
import at.bioinform.lucene.segment.Segment
import org.apache.lucene.document.{Document, Field, TextField}

object DocumentFlow {

  val IdentifierFieldName = "id"

  def identifierField(segment: Segment) = new Field(IdentifierFieldName, segment.id.value, TextField.TYPE_STORED)

  val SequenceFieldName = "sequence"

  def sequenceField(segment: Segment) = new Field(SequenceFieldName, segment.sequence.value, TextField.TYPE_STORED)

  def apply() = Flow[Segment].map(seg => {
    val document = new Document()
    document.add(identifierField(seg))
    document.add(sequenceField(seg))
    document
  })

}
