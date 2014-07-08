package org.scalax.semweb.shex

import org.scalax.semweb.rdf._


sealed trait NameClass extends ToQuads with ToTriplets with WithPatterns

object NameTerm {
  val property: IRI =rs / "propDefinition"
}


/**
 * IRI of the property
 * @param t IRI
 */
case class NameTerm(t: IRI) extends NameClass with ToQuads
{
  override def toQuads(subject: Res)(implicit context: Res = null)= Set(Quad(subject, NameTerm.property, t,context ))

  override def toTriplets(subject: Res): Set[Trip] = Set(Trip(subject,NameTerm.property,t))


}


case class NameAny(excl: Set[IRIStem]) extends NameClass{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???

  override def toTriplets(subject: Res): Set[Trip] = ???

}
object NameStem {

  val property: IRI = se / "stem"
}
case class NameStem(s: IRI) extends NameClass{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] =   Set(Quad(subject, NameStem.property , s,context ))

  override def toTriplets(subject: Res): Set[Trip] = Set(Trip(subject,NameStem.property, s))

}