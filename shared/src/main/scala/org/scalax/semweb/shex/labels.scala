package org.scalax.semweb.shex

import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.RDF
import org.scalax.semweb.sparql._




object Label {

  def apply(res:Res) = res match{
    case b:BlankNode=>BNodeLabel(b)
    case iri:IRI => IRILabel(iri)
  }
}

trait Label{

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

case class Action(label: Label, code: String)


