package org.scalax.semweb.shex

import org.scalax.semweb.rdf._
import org.scalax.semweb.sparql.{Pat, Variable}
import scala.util.{Failure, Success, Try}
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.Trip
import org.scalax.semweb.sparql.Variable
import org.scalax.semweb.sparql.Pat
import org.scalax.semweb.rdf.Quad


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

case class NameStem(s: IRI) extends NameClass{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] =   Set(Quad(subject, se / "stem", s,context ))

  override def toTriplets(subject: Res): Set[Trip] = ???

}