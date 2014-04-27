package org.scalax.semweb.shex

import org.scalax.semweb.rdf.{BlankNode, Quads, Quad, Res}
import org.scalax.semweb.rdf.vocabulary._

sealed trait Rule extends ToQuads

case class ArcRule(
                    id: Option[Label],
                    n: NameClass,
                    v: ValueClass,
                    c: Cardinality,
                    a: Seq[Action]
                    ) extends Rule
{
  override def toQuads(subj: Res)(implicit context: Res): Set[Quad] = {

   val me =  id.map(_.asResource).getOrElse(new BlankNode(Math.random().toString))



  //    Set( Quad(subj, rs / "property", model.sub, context))++ n.toQuads(me)++v.toQuads(me)++c.toQuads(me) //TODO: add actions somewhere
    ???
    //c.toQuads(subject)(context)
  }

  def makeId = Math.random().toString //todo: reimplement with something more reliable
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