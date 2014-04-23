package org.scalax.semweb.shex

import org.scalax.semweb.rdf.{Quad, BlankNode, Res, IRI}
import org.scalax.semweb.shex.parser.PrefixMap


case class Schema(pm: PrefixMap, rules: Seq[Shape])

case class ShEx(rules:Seq[Shape], start: Option[Label])

case class Shape(label: Label, rule: Rule) {


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