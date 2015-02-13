package org.scalax.semweb.sesame.shapes

import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{RDF, RDFS}
import org.scalax.semweb.shex._
import org.scalax.semweb.sparql._


trait ArcPropertiesQueryExtractor extends QueryExtractor
{

  protected def arcCaption(arc:ArcRule) = if(arc.title.isDefined) arc.title.get.replace(" ","_") else arc.name match {
    case NameTerm(iri)=>iri.localName
    case NameStem(stem)=>"stem_"+stem.localName
    case NameAny(any)=> "any_"+any.hashCode()
  }

  def arcNamePatterns(arc:ArcRule,sub:Variable = Variable("sub"),pred:Variable = ?("pred"),obj:Variable = ?("obj")) = arc.name match {

    case NameTerm(t)=>
      Seq(Pat(sub,t,obj),BIND(t,pred))

    case NameStem(stem)=>
      Seq(
        Pat(sub,pred,obj),
        FILTER(STR_STARTS(STR(pred),stem.stringValue))
      )

    case NameAny(any)=>
      val pats: Set[RDFElement] = any.map(s=>FILTER(NOT(STR_STARTS(STR(pred),s.s.stringValue))):RDFElement)+Pat(sub,pred,obj)
      pats.toSeq
  }

  def arcValuePatters(arc:ArcRule,sub:Variable = Variable("subject"),obj:Variable = ?("object")) =  arc.value match {
    case ValueStem(stem) =>
      Seq(
        FILTER(IsIRI(obj)),
        FILTER(STR_STARTS(STR(obj),stem.uri))
      )
    case ValueType(tp) if tp.stringValue.startsWith(vocabulary.XSD.namespace)=>
      Seq(
        FILTER(IsLiteral(obj)),
        FILTER(DATATYPE(obj,tp))
      )

    case ValueType(tp) if tp == vocabulary.RDF.VALUE=>
      Seq(Pat(sub,?("any"),obj))

    case ValueType(tp) if tp == vocabulary.RDFS.RESOURCE =>
      Seq(
        FILTER(NOT(IsLiteral(obj)))
      )

    case ValueType(other) =>
      val subType = Br(
        Pat(obj,RDF.TYPE,obj)
      )
      val subClass = Br(
        Pat(obj,RDFS.SUBCLASSOF,obj))
      Seq(Union(subType,subClass))

    case ValueSet(set)=>
      Seq(
        FILTER(IN(obj,set.map(v=>v:RDFElement)))
      )
  }

  def arcQuery(arc:ArcRule,sub:Variable = Variable("sub"),pred:Variable = ?("pred"),obj:Variable = ?("obj")):SelectQuery = {

    val namePattern = this.arcNamePatterns(arc,sub,pred,obj)
    val valuePattern = this.arcValuePatters(arc,sub,obj)
    val arcPatterns = namePattern++valuePattern
    SELECT (sub,pred,obj) WHERE(arcPatterns:_*)
  }

}
