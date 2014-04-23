package org.scalax.semweb.shex

import org.scalax.semweb.rdf.{Quad, Res}

sealed trait Rule extends ToQuads

case class ArcRule(
                    id: Option[Label],
                    n: NameClass,
                    v: ValueClass,
                    c: Cardinality,
                    a: Seq[Action]
                    ) extends Rule
{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???
}

case class AndRule(conjoints: Seq[Rule]) extends Rule
{
  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] = ???

}
case class OrRule(disjoints: Seq[Rule]) extends Rule {

  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] = ???

}
case class GroupRule(rule: Rule, opt: Boolean, a: Seq[Action]) extends Rule
{
  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] = ???

}