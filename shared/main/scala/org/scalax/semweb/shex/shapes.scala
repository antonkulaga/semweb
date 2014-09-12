package org.scalax.semweb.shex

import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{WI, RDF}


case class ShEx(rules:Seq[Shape], start: Option[Label] = None) extends {

}

object Shape {

  val rdfType = rs / "ResourceShape"

  lazy val empty = Shape(WI.PLATFORM.EMPTY, AndRule.empty)
}

/**
 * Shape expression
 * @param id
 * @param rule
 */
case class Shape(id: Label, rule: Rule)  extends Labeled
{


  /**
   * Turns shape into quad
   * @param context
   * @return
   */
  def asQuads(implicit context:Res):Set[Quad] =
  {
    val model = Quads -- id.asResource
    model -- RDF.TYPE -- Shape.rdfType -- context

    model.quads ++ rule.toQuads(model.sub)(context)
  }


  def asPropertyModel(implicit context:Res):PropertyModel = {
    val res = id.asResource
    val props: Map[IRI, Set[RDFValue]] = rule.toQuads(res)(context).map(q=>(q.pred,Set(q.obj))).toMap
    PropertyModel(res,props)

  }


  def loadProperties(res:Res) =  this.rule match {
      //TODO: delete or figure out what I need it for

    case and:AndRule=> and.conjoints.map{


      case arc:ArcRule=>

      case r =>print(s"nonArc conjoints are not yet supported, passed rule is ${r.toString}")
    }

    case r => print(s"or rule is not yet supported, passed rule is ${r.toString}")
  }

  def arcSorted()= this.arcRules(this.rule).sortBy(f=>f.priority)

  def arcRules(rl:Rule = this.rule):List[ArcRule] = {
    rl match {
      case arc:ArcRule=> List(arc)
      case  and:AndRule=> and.conjoints.flatMap(v=>arcRules(v)).toList
      case  orRule:OrRule=> orRule.disjoints.flatMap(v=>arcRules(v)).toList //TODO: figure out what to do with ors
      case _=> List.empty[ArcRule]
    }

  }

  def updated[TR<:Rule](change:PartialFunction[Rule,TR]) =  if(change.isDefinedAt(rule))
    Some(this.copy(rule = change(rule)))
  else  rule match
  {
    case and:AndRule=>
    case orRule:OrRule=>
    case _=> None
  }


  def updated(newRule:Rule):Option[Shape] = this.rule match {
    case r if this.id.asResource==newRule.id.asResource=> Some(Shape(newRule.id,newRule))
    case r if r.id==newRule.id && r!=newRule=> Some(this.copy(rule = newRule))
    case and:AndRule=> and.updated(newRule).map(r=>this.copy(rule = r))

    case or:OrRule=> or.updated(newRule).map(r=>this.copy(rule = r))
    case _ => None

  }



}