package org.scalax.semweb.shex
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{RDFS, WI, RDF}
import org.scalax.semweb.sparql.{GP, Variable}
import org.scalax.semweb.rdf.{Trip, Quad, Res}
import org.scalax.semweb.rdf.vocabulary.WI



trait RuleContainer extends Rule {
  def items:Set[Rule]

}

object AndRule
{

  lazy val empty = new AndRule(Set.empty,WI.PLATFORM.EMPTY)

}


/**
 * TODO: decide about the type
 * @param conjoints
 */
case class AndRule(conjoints: Set[Rule], id:Label) extends RuleContainer
{
  self=>
  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] =  this.conjoints.flatMap(c=>c.toQuads(subject)(context)).toSet

  override def toTriplets(subject: Res): Set[Trip] = this.conjoints.flatMap(c=>c.toTriplets(subject)).toSet

  def items = conjoints

  def updated(rule:Rule):AndRule =   this.items.find(c=>c.id.asResource==rule.id.asResource)
    .map(r=>this.copy(conjoints = conjoints - r + rule)).getOrElse(this + rule)

  def ++(rules:Seq[Rule]) = this.copy(conjoints = self.conjoints++rules)

  def ++(rules:Set[Rule]) = this.copy(conjoints = self.conjoints++rules)


  def + (rule:Rule) = this.copy(conjoints = self.conjoints + rule)

  def - (rule:Rule) = this.copy(conjoints = self.conjoints - rule)

}


case class OrRule(disjoints: Set[Rule], id:Label = Rule.genRuleLabel()) extends RuleContainer {

  self=>

  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] = ???

  override def toTriplets(subject: Res): Set[Trip] = ???

  def ++(rules:Seq[Rule]) = this.copy(disjoints = self.disjoints++rules)

  def ++(rules:Set[Rule]) = this.copy(disjoints = self.disjoints++rules)

  def + (rule:Rule) = this.copy(disjoints = self.disjoints + rule)

  def - (rule:Rule) = this.copy(disjoints = self.disjoints - rule)

  def updated(rule:Rule):OrRule =   this.disjoints.find(c=>c.id.asResource==rule.id.asResource)
    .map(r=>this.copy(disjoints = disjoints - r + rule)).getOrElse(this + rule)

  def items = disjoints

}

case class GroupRule(rule: Rule, opt: Boolean, a: Set[Action], id:Label = Rule.genRuleLabel()) extends Rule
{
  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] = ???

  override def toTriplets(subject: Res): Set[Trip] = ???
}


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

