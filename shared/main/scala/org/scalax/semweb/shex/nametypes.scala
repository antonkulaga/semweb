package org.scalax.semweb.shex

import org.scalax.semweb.rdf._


sealed trait NameClass extends ToQuads with ToTriplets with WithPatterns
{
  def matches(iri:IRI):Boolean

}

object NameTerm {
  val property: IRI =rs / "propDefinition"
}


/**
 * IRI of the property
 * @param property IRI
 */
case class NameTerm(property: IRI) extends NameClass with ToQuads
{
  override def toQuads(subject: Res)(implicit context: Res = null)= Set(Quad(subject, NameTerm.property, property,context ))

  override def toTriplets(subject: Res): Set[Trip] = Set(Trip(subject,NameTerm.property,property))

  override def matches(iri: IRI): Boolean = iri == property
}


case class NameAny(excl: Set[NameStem]) extends NameClass{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???

  override def toTriplets(subject: Res): Set[Trip] = ???

  override def matches(iri: IRI): Boolean = !excl.exists(e=>e.matches(iri))
  //TODO: look at AST and IRIstem, what is the difference?
}
object NameStem {

  val property: IRI = se / "stem"


}
case class NameStem(s: IRI) extends NameClass{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] =   Set(Quad(subject, NameStem.property , s,context ))

  override def toTriplets(subject: Res): Set[Trip] = Set(Trip(subject,NameStem.property, s))

  override def matches(iri: IRI): Boolean = iri.stringValue.contains(s.stringValue)
}