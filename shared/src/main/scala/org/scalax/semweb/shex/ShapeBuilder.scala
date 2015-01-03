package org.scalax.semweb.shex

import org.scalax.semweb.rdf.vocabulary.{RDFS, RDF}
import org.scalax.semweb.rdf.{RDFBuilder, Res, IRI}
import scala.collection.immutable._

/**
 * For nice shape building
 * @param propertyIRI
 */
class WithShapeProperty(id:Res = Rule.genRuleRes(),propertyIRI:IRI) {
  protected var occ: Cardinality = Plus
  protected var vc: ValueClass = ValueType(RDFS.RESOURCE)
  protected var priority: Option[Int] = None
  protected var title: Option[String] = None
  lazy val result: Option[ArcRule] = Some(ArcRule(Label.apply(id), propertyIRI, vc, occ, Seq.empty, this.priority, this.title))


  def from(vals:IRI*) = {
    vc = ValueSet(vals.toSet)
    this
  }

  def startsWith(start:IRI) = {
    vc = ValueStem(start)
    this
  }


  def of(tp: IRI) = {
    vc = res2ValueType(tp)
    this
  }

  def oneOf(params: IRI*) = {
    vc = ValueSet(params.toSet)
    this
  }

  def occurs(c: Cardinality) = {
    occ = c
    this
  }

  def occurs(from:Int,to:Int) = {
    occ = Cardinality(from,to)
    this
  }

  def isCalled(tlt:String) = {
    title = Some(tlt)
    this
  }

  def hasPriority(prior: Int) = {
    this.priority = Some(prior)
    this
  }

  def hasNoPriority = {
    this.priority = None
    this
  }


}
/**
 * Quad builder
 */
class ShapeBuilder(res:Res) extends RDFBuilder[WithShapeProperty]{

  def has(iri:IRI) = this -- new WithShapeProperty(propertyIRI = iri)

  def hasProperty(res:Res,iri:IRI) = this -- new WithShapeProperty(res,iri)

  def hasRule(rule:Rule):this.type  = {
    this.rules = this.rules + rule
    this
  }

  //TODO: rewrite
  protected var rules:Set[Rule] = Set.empty




  /**
   * Creates shape
   * @return
   */
  def result:Shape = {
    val allRules: Set[Rule] = this.values.filter(_.result.isDefined).map(_.result.get) ++ this.rules
    val and: AndRule = new AndRule(allRules,Label.apply(res))
    Shape(res,and)
  }



}