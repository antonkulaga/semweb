package org.scalax.semweb.shex

import org.scalax.semweb.rdf.{Res, IRI}

/**
 * The following definitions follow: http://www.w3.org/2013/ShEx/Definition
 * */
object ShapeSyntax {
  lazy val foaf = "http://xmlns.com/foaf/0.1/"
  lazy val xsd  = "http://www.w3.org/2001/XMLSchema#"
  lazy val shex = "http://www.w3.org/2013/ShEx/ns#"
  lazy val typeShexLiteral  	= ValueType(v = IRI(shex + "Literal"))
  lazy val typeShexIRI  		= ValueType(v = IRI(shex + "IRI"))
  lazy val typeShexBNode  	= ValueType(v = IRI(shex + "BNode"))
  lazy val typeShexNonLiteral	= ValueType(v = IRI(shex + "NonLiteral"))
  lazy val typeXsdString		= ValueType(v = IRI(xsd  + "string"))
  lazy val NoActions : Seq[Action] = Seq.empty[Action]


  def range(m: Long, n: Long): Cardinality = {
    require(n > m)
    Cardinality(min = m, max = n)
  }

  /** Utility to generate rules from arcs */
  def envolve(s: ArcRule): Rule = OrRule(List(AndRule(List(s))))




  case class ShEx(rules:Seq[Shape], start: Option[Label])

  case class Shape(label: Label, rule: Rule)

  sealed trait Rule

  case class ArcRule(
      id: Option[Label],
      n: NameClass,
      v: ValueClass,
      c: Cardinality,
      a: Seq[Action]
      ) extends Rule
  {

  }

    case class AndRule(conjoints: Seq[Rule]) extends Rule
    case class OrRule(disjoints: Seq[Rule]) extends Rule
    case class GroupRule(rule: Rule, opt: Boolean, a: Seq[Action]) extends Rule

    sealed trait Label
    case class IRILabel(iri: IRI) extends Label
    case class BNodeLabel(bnodeId: Int) extends Label

    case class IRIStem(iri: IRI, isStem: Boolean)

    sealed trait NameClass
    case class NameTerm(t: IRI) extends NameClass
    case class NameAny(excl: Set[IRIStem]) extends NameClass
    case class NameStem(s: IRI) extends NameClass

    sealed trait ValueClass
    case class ValueType(v: Res) extends ValueClass
    case class ValueSet(s: Seq[Res]) extends ValueClass
    case class ValueAny(stem: IRIStem) extends ValueClass
    case class ValueStem(s: IRI) extends ValueClass
    case class ValueReference(l: Label) extends ValueClass

    case class Action(label: Label, code: String)



  object Bound {
    def apply(limit:Long) = new Bound(limit)
    case object Zero extends Bound(0)
    case object Once extends Bound(1)
    case object Unbound extends Bound(Long.MaxValue)


  }
  class Bound(val limit:Long) {
    def isUnbound = limit==Long.MaxValue
    def isZero = limit==0
  }


  class Cardinality(min: Bound,max: Bound){
    require(min.limit<=max.limit) //check that minimum is lower that maximum
  }
  object Cardinality
  {
    def apply(min: Bound,max: Bound) = new Cardinality(min,max)
    def apply(min: Long,max:Long) = new Cardinality(Bound(min),Bound(max))

  }
  // Utility definitions


  import Bound._

  object ExactlyOne extends Cardinality(Once,Once)
  object Plus extends Cardinality(Once,Unbound)
  object Start extends Cardinality(Zero,Unbound)
  object Opt extends Cardinality(Zero,Once)

  // lazy val NoId : Label = IRILabel(iri = IRI(""))

}