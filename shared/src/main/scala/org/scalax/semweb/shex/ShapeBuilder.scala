package org.scalax.semweb.shex

import javax.security.auth.Subject

import org.scalax.semweb.rdf.vocabulary.{WI, RDFS, RDF}
import org.scalax.semweb.rdf.{RDFBuilder, Res, IRI}
import scala.collection.immutable._


trait WithValueProperty {
  protected var vc: ValueClass = ValueType(RDFS.RESOURCE)

  type This = this.type 

  def from(vals:IRI*):This = {
    vc = ValueSet(vals.toSet)
    this
  }

  def startsWith(start:IRI):This = {
    vc = ValueStem(start)
    this
  }


  def of(tp: IRI):This = {
    vc = res2ValueType(tp)
    this
  }

  def oneOf(params: IRI*):This = {
    vc = ValueSet(params.toSet)
    this
  }


}
trait WithTitledProperty {
  protected var title: Option[String] = None
  
  type This = this.type 

  def isCalled(tlt:String):This = {
    title = Some(tlt)
    this
  }

}


/**
 * For nice shape building
 * @param propertyIRI
 */
class WithShapeProperty(id:Res = Rule.genRuleRes(),propertyIRI:IRI) extends WithValueProperty with WithTitledProperty{
  protected var occ: Cardinality = Plus
  protected var priority: Option[Int] = None
  lazy val result: Option[ArcRule] = Some(ArcRule(Label.apply(id), propertyIRI, vc, occ, Seq.empty, this.priority, this.title))

  override type This = this.type

  def occurs(c: Cardinality) = {
    occ = c
    this
  }

  def occurs(from:Int,to:Int) = {
    occ = Cardinality(from,to)
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