package org.scalax.semweb.shex

import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{WI, RDF}


object ShEx {

  val rdtType = se / "Shape_Expression"

  val hasShape = se / "has_shape"

  val start = se / "start"

  val startTitle = se / "start_title" //in this way we define how the start resource will be called
}

/**
 * Shape expressions that contains different shapes
 * @param rules shapes inside
 * @param start start shape [optional]
 * @param title title of subject
 */
case class ShEx(id:Label,rules:Seq[Shape], start: Option[Label] = None, title:Option[String] = None)   extends Labeled

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
  
  def + (r:Rule) = rule match {
    case and:AndRule => this.copy(rule = and + r)
    case or:OrRule => this.copy(rule = or + r)
    case other => this.copy(rule = AndRule(Set(rule,other),id))
  }
  
  def - (r:Rule) = rule match {
    case and:AndRule => this.copy(rule = and - r)
    case or:OrRule => this.copy(rule = or - r)
    case other => if(other.id==r.id) this.copy(rule = AndRule.empty)
  }
  
  def updated(r:Rule):Shape = this.rule match {
    case and:AndRule => copy(rule = and.updated(r))
    case or:OrRule => copy(rule = or.updated(r))
    case other => if(other.id==r.id) copy(rule = r) else this
  }


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

  def expand[T](fun:PartialFunction[Rule,List[T]]):PartialFunction[Rule,List[T]] = {
    case  and:AndRule=> and.conjoints.flatMap(v=>fold[T](v)(fun)).toList
    case  orRule:OrRule=> orRule.disjoints.flatMap(v=>fold[T](v)(fun)).toList //TODO: figure out what to do with ors
    case _=>List.empty[T]
  }

  /**
   * Folds all rules
   * @param rl start Rule
   * @param fun partial function that will be expanded
   * @tparam T type parameter to collect
   * @return List of type parameters
   */
  def fold[T](rl:Rule = this.rule)(fun:PartialFunction[Rule,List[T]]):List[T] = fun.orElse(expand[T](fun))(rl)


  def arcRules(rl:Rule = this.rule):List[ArcRule] = {
    rl match {
      case arc:ArcRule=> List(arc)
      case  and:AndRule=> and.conjoints.flatMap(v=>arcRules(v)).toList
      case  orRule:OrRule=> orRule.disjoints.flatMap(v=>arcRules(v)).toList //TODO: figure out what to do with ors
      case _=> List.empty[ArcRule]
    }

  }

  def updated[TR<:Rule](change:PartialFunction[Rule,TR]) =  if(change.isDefinedAt(rule))
    this.copy(rule = change(rule)) else this

/*
  def updated(newRule:Rule):Option[Shape] = this.rule match {
    case r if this.id.asResource==newRule.id.asResource=> Some(Shape(newRule.id,newRule))
    case r if r.id==newRule.id && r!=newRule=> Some(this.copy(rule = newRule))
    case and:AndRule=> and.updated(newRule).map(r=>this.copy(rule = r))

    case or:OrRule=> or.updated(newRule).map(r=>this.copy(rule = r))
    case _ => None

  }*/



}