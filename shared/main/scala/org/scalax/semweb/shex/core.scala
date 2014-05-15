package org.scalax.semweb.shex

import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary._

import org.scalax.semweb.shex.parser.PrefixMap
import org.scalax.semweb.rdf.vocabulary.{RDF, FOAF}
import org.scalax.semweb.sparql.Pat

import org.scalax.semweb.sparql._
import scala.util.Try



trait ToTriplets{
  def toTriplets(subject:Res):Set[Trip]
}

trait ToQuads
{
  def toQuads(subject:Res)(implicit context:Res = null):Set[Quad]
}

trait WithPatterns {
  def empty:PatternResult = (Set.empty[Pat],Map.empty[String,Variable])

  type PatternResult = (Set[Pat],Map[String,Variable])

}

trait ToPatterns extends WithPatterns
{
  /**
   * Tries to make patterns with provided variables
   * @param res
   * @return
   */
  //def toPatterns(res:Res,vars:Map[String,Variable] = Map.empty):Try[Set[Pat]]

  def toPatterns(res:Res):PatternResult


}

case class Schema(pm: PrefixMap, rules: Seq[Shape])

case class ShEx(rules:Seq[Shape], start: Option[Label] = None)

object Shape {

  val rdfType = rs / "ResourceShape"

}


case class Shape(label: Label, rule: Rule) {

  /**
   * Turns shape into quad
   * @param context
   * @return
   */
  def asQuads(implicit context:Res):Set[Quad] =
  {
    val model = Quads -- label.asResource
    model -- RDF.TYPE -- Shape.rdfType -- context

    model.quads ++ rule.toQuads(model.sub)(context)
  }

  def loadProperties(res:Res) =  this.rule match {
    case and:AndRule=> and.conjoints.map{
      case arc:ArcRule=>

      case r =>print(s"nonArc conjoints are not yet supported, passed rule is ${r.toString}")
    }
    case r => print(s"or rule is not yet supported, passed rule is ${r.toString}")
  }


}

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

case class IRIStem(iri: IRI, isStem: Boolean)

case class Action(label: Label, code: String)