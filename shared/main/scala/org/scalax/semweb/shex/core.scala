package org.scalax.semweb.shex

import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary._

import org.scalax.semweb.shex.parser.PrefixMap
import org.scalax.semweb.rdf.vocabulary.{RDF, FOAF}


case class Schema(pm: PrefixMap, rules: Seq[Shape])

case class ShEx(rules:Seq[Shape], start: Option[Label] = None)




case class Shape(label: Label, rule: Rule) {

  /**
   * Turns shape into quad
   * @param context
   * @return
   */
  def asQuads(implicit context:Res):Set[Quad] =
  {
    val model = Quads -- label.asResource
    model -- RDF.TYPE -- rs / "ResourceShape" -- context

    model.quads ++ rule.toQuads(model.sub)
  }


}


sealed trait Label{

  def asResource:Res
}

case class IRILabel(iri: IRI) extends Label
{
  def asResource:Res = iri

}
case class BNodeLabel(bnode:BlankNode) extends Label
{
  def asResource:Res = bnode

}

case class IRIStem(iri: IRI, isStem: Boolean)




trait ToQuads
{
  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad]
}

case class Action(label: Label, code: String)