package org.scalax.semweb.shex

import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{WI, RDF}
import org.scalax.semweb.sparql.{GP, Variable}



trait ToGroupPattern {

  def toGroupPattern(res:Res):(GP,Set[Variable])
}

trait Labeled {
  def id:Label
}

object Rule {
  def genRuleRes():BlankNode = new BlankNode(Math.random().toString)

  def genRuleLabel():Label = BNodeLabel( genRuleRes())
}

trait Rule extends ToQuads with ToTriplets with Labeled


object ArcRule {

  val property = rs / "property"

  val priority: IRI = WI.pl("priority")


  def apply(propertyName:IRI): ArcRule = apply(propertyName,RDF.VALUE,Star,None)

  def apply(propertyName:IRI,tp:IRI): ArcRule =   apply(propertyName,tp,Star,None)

  def apply(propertyName:IRI,tp:IRI,card:Cardinality): ArcRule = apply(propertyName,tp,card,None)

  def apply(propertyName:IRI,tp:IRI,card:Cardinality, priority:Option[Int]): ArcRule =
  {
    ArcRule(Rule.genRuleLabel(), NameTerm(propertyName),ValueType(tp),card, priority = priority)
  }

}



case class ArcRule(
                    id: Label =Rule.genRuleLabel(),
                    name: NameClass,
                    value: ValueClass,
                    occurs: Cardinality,
                    actions: Seq[Action] = List.empty,
                    priority:Option[Int] = None, //the smaller the more important
                    title:Option[String] = None
                    ) extends Rule
{

  lazy val me = id.asResource

//  override def toQuads(subj: Res)(implicit context: Res): Set[Quad] = {
//   Set(Quad(subj, ArcRule.property, me, context))++ name.toQuads(me)(context) ++ value.toQuads(me)(context) ++  occurs.toQuads(me)(context)
//  }

    override def toQuads(subj: Res)(implicit context: Res): Set[Quad] = {
      val tlt: Set[Quad] =  this.title.fold(Set.empty[Quad])(t=>Set(Quad(me, vocabulary.DCElements.title,t,context)))
      val prior: Set[Quad] = this.priority.fold(Set.empty[Quad])(p=>Set(Quad(me, ArcRule.priority,IntLiteral(p),context)))

      Set(Quad(subj, ArcRule.property, me, context)) ++ tlt ++ prior ++   name.toQuads(me)(context) ++ value.toQuads(me)(context) ++  occurs.toQuads(me)(context)

    }


  override def toTriplets(subj:Res):Set[Trip] = {

    val tlt: Set[Trip] =  this.title.fold(Set.empty[Trip])(t=>Set(Trip(me, vocabulary.DCElements.title,t)))
    val prior: Set[Trip] = this.priority.fold(Set.empty[Trip])(p=>Set(Trip(me, ArcRule.priority,IntLiteral(p))))
    Set(Trip(subj, ArcRule.property, me))++ tlt ++ prior   ++  name.toTriplets(me) ++ value.toTriplets(me) ++  occurs.toTriplets(me)
  }

//  def occurs(occ:Cardinality) = this.copy(occurs = occ)

  def makeId = Math.random().toString //todo: reimplement with something more reliable

  def propertiesByNameClass(properties:Map[IRI,RDFValue]) = properties.filter(kv=>name.matches(kv._1))


}

trait RuleContainer extends Rule {
  def items:Set[Rule]



 // def updated(rule:Rule):Option[this.type ]

}

object AndRule
{

  def empty = new AndRule(Set.empty,WI.PLATFORM.EMPTY)

}


/**
 * TODO: decide about the type
 * @param conjoints
 */
case class AndRule(conjoints: Set[Rule], id:Label) extends RuleContainer
{
  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] =  this.conjoints.flatMap(c=>c.toQuads(subject)(context)).toSet

  override def toTriplets(subject: Res): Set[Trip] = this.conjoints.flatMap(c=>c.toTriplets(subject)).toSet

  def items = conjoints

 def updated(rule:Rule):Option[AndRule] =   this.items.find(c=>c.id.asResource==rule.id.asResource)
  .map(r=>this.copy(conjoints = conjoints - r + rule))
}


case class OrRule(disjoints: Set[Rule], id:Label = Rule.genRuleLabel()) extends RuleContainer {

  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] = ???

  override def toTriplets(subject: Res): Set[Trip] = ???

  def items = disjoints

   def updated(rule: Rule): Option[OrRule] =   this.items.find(c=>c.id.asResource==rule.id.asResource)
    .map(r=>this.copy(disjoints = disjoints - r + rule))
}

case class GroupRule(rule: Rule, opt: Boolean, a: Set[Action], id:Label = Rule.genRuleLabel()) extends Rule
{
  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] = ???

  override def toTriplets(subject: Res): Set[Trip] = ???
}