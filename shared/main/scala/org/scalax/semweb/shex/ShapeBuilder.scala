package org.scalax.semweb.shex

import org.scalax.semweb.rdf.{RDFBuilder, Res, IRI}
import scala.collection.immutable._

/**
 * For nice shape building
 * @param iri
 */
class WithShapeProperty(id:Option[Res],iri:IRI)
{

  lazy val result:Option[ArcRule] = if(vc == null) None else Some(ArcRule(id.map(Label.apply),iri,vc,occ))


  protected var occ:Cardinality = ExactlyOne
  protected var vc:ValueClass = null


  def of(tp:IRI) = {
    vc =tp
    this
  }

  def oneOf(params:IRI*) = {
    vc = ValueSet(params.toSet)
    this
  }

  def occurs(c:Cardinality) = {
    occ = c
    this
  }



}
/**
 * Quad builder
 */
class ShapeBuilder(res:Res) extends RDFBuilder[WithShapeProperty]{

  def has(iri:IRI) = this -- new WithShapeProperty(None,iri)

  def hasProperty(res:Res,iri:IRI) = this -- new WithShapeProperty(Some(res),iri)

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
    val and = new AndRule(allRules)
    Shape(res,and)
  }



}