package org.scalax.semweb.shex

import org.scalax.semweb.rdf.{BlankNode, Quads, Quad, Res}
import org.scalax.semweb.rdf.vocabulary._

sealed trait Rule extends ToQuads


object ArcRule {

  val property = rs / "property"

}


case class ArcRule(
                    id: Option[Label],
                    name: NameClass,
                    value: ValueClass,
                    occurs: Cardinality,
                    actions: Seq[Action] = List.empty
                    ) extends Rule
{

  lazy val me = id.map(_.asResource).getOrElse(new BlankNode(Math.random().toString))

  override def toQuads(subj: Res)(implicit context: Res): Set[Quad] = {

   Set(Quad(subj, ArcRule.property, me, context))++ name.toQuads(me)(context) ++ value.toQuads(me)(context) ++  occurs.toQuads(me)(context)
  }

//  def occurs(occ:Cardinality) = this.copy(occurs = occ)

  def makeId = Math.random().toString //todo: reimplement with something more reliable
}

/**
 * TODO: decide about the type
 * @param conjoints
 */
case class AndRule(conjoints: Set[Rule]) extends Rule
{
  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] =  this.conjoints.flatMap(c=>c.toQuads(subject)(context)).toSet

}
case class OrRule(disjoints: Set[Rule]) extends Rule {

  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] = ???

}
case class GroupRule(rule: Rule, opt: Boolean, a: Set[Action]) extends Rule
{
  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] = ???

}