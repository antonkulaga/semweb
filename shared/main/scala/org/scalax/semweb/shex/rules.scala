package org.scalax.semweb.shex

import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{WI, RDF}
import org.scalax.semweb.sparql.{GP, Variable}


trait ToGroupPatter {

  def toGroupPattern(res:Res):(GP,Set[Variable])
}

sealed trait Rule extends ToQuads with ToTriplets


object ArcRule {

  val property = rs / "property"
  val priority: IRI = WI.pl("priority")

}
object AndRule{

  def apply(propertyName:IRI,tp:IRI = RDF.VALUE,card:Cardinality = Star,priority:Option[Int] = None) = {
    ArcRule(None, NameTerm(propertyName),ValueType(tp),card, priority = priority)
  }
}


case class ArcRule(
                    id: Option[Label],
                    name: NameClass,
                    value: ValueClass,
                    occurs: Cardinality,
                    actions: Seq[Action] = List.empty,
                    priority:Option[Int] = None, //the smaller the more important
                    title:Option[String] = None
                    ) extends Rule
{

  lazy val me = id.map(_.asResource).getOrElse(new BlankNode(Math.random().toString))

//  override def toQuads(subj: Res)(implicit context: Res): Set[Quad] = {
//   Set(Quad(subj, ArcRule.property, me, context))++ name.toQuads(me)(context) ++ value.toQuads(me)(context) ++  occurs.toQuads(me)(context)
//  }

    override def toQuads(subj: Res)(implicit context: Res): Set[Quad] = {
      val tlt: Set[Quad] =  this.title.fold(Set.empty[Quad])(t=>Set(Quad(me, vocabulary.DCElements.title,t,context)))
      val prior: Set[Quad] = this.priority.fold(Set.empty[Quad])(p=>Set(Quad(me, ArcRule.priority,IntegerLiteral(p),context)))

      Set(Quad(subj, ArcRule.property, me, context)) ++ tlt ++ prior ++   name.toQuads(me)(context) ++ value.toQuads(me)(context) ++  occurs.toQuads(me)(context)

    }


  override def toTriplets(subj:Res):Set[Trip] = {

    val tlt: Set[Trip] =  this.title.fold(Set.empty[Trip])(t=>Set(Trip(me, vocabulary.DCElements.title,t)))
    val prior: Set[Trip] = this.priority.fold(Set.empty[Trip])(p=>Set(Trip(me, ArcRule.priority,IntegerLiteral(p))))
    Set(Trip(subj, ArcRule.property, me))++ tlt ++ prior   ++  name.toTriplets(me) ++ value.toTriplets(me) ++  occurs.toTriplets(me)
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

  override def toTriplets(subject: Res): Set[Trip] = this.conjoints.flatMap(c=>c.toTriplets(subject)).toSet
}
case class OrRule(disjoints: Set[Rule]) extends Rule {

  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] = ???

  override def toTriplets(subject: Res): Set[Trip] = ???
}
case class GroupRule(rule: Rule, opt: Boolean, a: Set[Action]) extends Rule
{
  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad] = ???

  override def toTriplets(subject: Res): Set[Trip] = ???
}