package org.denigma.semweb.shex

import org.denigma.semweb.rdf._
import org.denigma.semweb.rdf.vocabulary.WI

object ValueClass {
  //val property = WI.pl("value_of")
  //val clazz = se / "ValueClass"
  
}


sealed trait ValueClass extends ToQuads with ToTriplets
{

}

object ValueType {
  val property = rs / "valueType"
  //val clazz = se / "ValueType"
}

case class ValueType(v: Res) extends ValueClass{

  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
    Set(Quad(subject, ValueType.property, v, context))
  }

  override def toTriplets(subject: Res): Set[Trip] = Set(Trip(subject,ValueType.property, v))

}

object ValueSet {

  val property: IRI =  rs / "allowedValue"
  
  //val clazz = se / "ValueSet"
  
}

case class ValueSet(s: Set[RDFValue]) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {

    val prop =  Quads -- subject -- ValueSet.property
    s.map(v=>prop -- v -- context).toSet[Quad]
  }

  override def toTriplets(subject: Res): Set[Trip] = s.map{s=>
    Trip(subject,ValueSet.property,s)
  }

}


object ValueAny {
  
  val clazz = se / "ValueAny"

  
}

case class ValueAny(stem: ValueStem) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???

  override def toTriplets(subject: Res): Set[Trip] = ???


}

object ValueStem {
  val property = se / "stem"
  
  //val clazz = se / "ValueStem"
}

case class ValueStem(s: IRI) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
    Set(Quad(subject,ValueStem.property, s, context))
  }

  override def toTriplets(subject: Res): Set[Trip] =Set( Trip(subject, ValueStem.property,s) )

  def matches(iri: IRI): Boolean = iri.stringValue.startsWith(s.stringValue)

}
object ValueReference {
  val property = se / "valueShape"
}

case class ValueReference(l: Label) extends ValueClass {

  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
    Set(Quad(subject,ValueReference.property, l.asResource, context))
  }

  override def toTriplets(subject: Res): Set[Trip] = {
    Set(Trip(subject,ValueReference.property, l.asResource))
  }

}
