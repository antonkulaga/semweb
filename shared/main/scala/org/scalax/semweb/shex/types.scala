package org.scalax.semweb.shex
import org.scalax.semweb.rdf.vocabulary._

import org.scalax.semweb.rdf.{Quads, Quad, Res, IRI}

sealed trait NameClass extends ToQuads

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
}


case class NameAny(excl: Set[IRIStem]) extends NameClass{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???
}

case class NameStem(s: IRI) extends NameClass{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] =   Set(Quad(subject, se / "stem", s,context ))
}

sealed trait ValueClass extends ToQuads
{

}

object ValueType {
 val property = rs / "valueType"
}

case class ValueType(v: Res) extends ValueClass{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
   Set(Quad(subject,rs / "valueType", v, context))
  }
}

case class ValueSet(s: Set[Res]) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {

    val prop =  Quads -- subject -- rs / "allowedValue"
    s.map(v=>prop -- v -- context).toSet[Quad]
  }
}
case class ValueAny(stem: IRIStem) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???
}
case class ValueStem(s: IRI) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
    Set(Quad(subject,se / "stem", s, context)) //TODO change
  }
}
case class ValueReference(l: Label) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
    Set(Quad(subject,se / "valueShape", l.asResource, context))
  }
}
