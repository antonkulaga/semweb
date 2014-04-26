package org.scalax.semweb.shex
import org.scalax.semweb.rdf.vocabulary._

import org.scalax.semweb.rdf.{Quad, Res, IRI}

sealed trait NameClass extends ToQuads

case class NameTerm(t: IRI) extends NameClass with ToQuads
{
  override def toQuads(subject: Res)(implicit context: Res = null)= Set(Quad(subject, rs / "name", t,context ))
}


case class NameAny(excl: Set[IRIStem]) extends NameClass{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???
}
case class NameStem(s: IRI) extends NameClass{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???
}

sealed trait ValueClass extends ToQuads
{

}

case class ValueType(v: Res) extends ValueClass{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???
}

case class ValueSet(s: Seq[Res]) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???
}
case class ValueAny(stem: IRIStem) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???
}
case class ValueStem(s: IRI) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???
}
case class ValueReference(l: Label) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???
}
