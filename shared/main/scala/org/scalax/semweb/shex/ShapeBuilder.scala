package org.scalax.semweb.shex

import org.scalax.semweb.rdf.{BlankNode, RDFBuilder, Res, IRI}
import scala.collection.immutable._
import org.scalax.semweb.shex.Shape
import org.scalax.semweb.shex.Shape

/**
 * For nice shape building
 * @param iri
 */
class WithShapeProperty(iri:IRI)
{

  def result:Option[Rule] = if(vc == null) None else Some(ArcRule(None,iri,vc,occ))


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

  def has(iri:IRI) = this -- new WithShapeProperty(iri)


  /**
   * Creates shape
   * @return
   */
  def result:Shape = {
    val rules: Set[Rule] = this.values.filter(_.result.isDefined).map(_.result.get)
    val and = new AndRule(rules)
    Shape(res,and)
  }



}