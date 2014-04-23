package org.scalax.semweb.shex
import org.scalax.semweb.rdf.vocabulary._

import org.scalax.semweb.rdf.{Quad, Res, IRI}

sealed trait NameClass

case class NameTerm(t: IRI) extends NameClass with ToQuads
{
  override def toQuads(subject: Res)(implicit context: Res = null)= Set(Quad(subject, rs / "name" iri, t,context ))
}


case class NameAny(excl: Set[IRIStem]) extends NameClass{

}
case class NameStem(s: IRI) extends NameClass

sealed trait ValueClass //extends ToQuads
case class ValueType(v: Res) extends ValueClass
case class ValueSet(s: Seq[Res]) extends ValueClass
case class ValueAny(stem: IRIStem) extends ValueClass
case class ValueStem(s: IRI) extends ValueClass
case class ValueReference(l: Label) extends ValueClass
