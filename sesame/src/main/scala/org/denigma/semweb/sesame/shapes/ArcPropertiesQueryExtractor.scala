package org.denigma.semweb.sesame.shapes

import org.denigma.semweb.rdf._
import org.denigma.semweb.rdf.vocabulary.{RDF, RDFS}
import org.denigma.semweb.shex
import org.denigma.semweb.shex._
import org.denigma.semweb.sparql._

trait ArcPropertiesQueryExtractor extends QueryExtractor
{

  protected def arcCaption(arc:ArcRule) = if(arc.title.isDefined) arc.title.get.replace(" ","_") else arc.name match {
    case NameTerm(iri)=>iri.localName
    case NameStem(stem)=>"stem_"+stem.localName
    case NameAny(any)=> "any_"+any.hashCode()
  }

  /**
   * * Works only partially for nonzero checking
   * TODO: add full support of onooptional things*
   * @param arc
   * @param patterns
   * @return
   */
  def withOccurence(arc:ArcRule,  patterns:Seq[RDFElement]): Seq[RDFElement] = 
    arc.occurs match {
    case ExactlyOne =>patterns
    case Star =>
      Seq(Optional(patterns:_*))
    case Plus => patterns
    case Opt =>
      Seq(Optional(patterns:_*))
    case shex.Range(min,max) if min==0=>
      Seq(Optional(patterns:_*))
    case shex.Range(min,max)=>patterns


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

  def suggestPatterns(typed:String, arc:ArcRule,sub:Variable = Variable("subject"),obj:Variable = ?("object"))= {
    arcNamePatterns(arc,sub,obj) ++ arcValuePatterns(arc,sub,obj) ++ containsPatterns(typed,obj)
  }

  def containsPatterns(typed:String,obj:Variable = ?("object")) = {
    Seq(FILTER(STR_CONTAINS(STR(obj),typed)))
  }

  def arcValuePatterns(arc:ArcRule,sub:Variable = Variable("subject"),obj:Variable = ?("object")) =  arc.value match {
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
      Seq(Pat(sub,?("any_property"),obj))

    case ValueType(tp) if tp == vocabulary.RDFS.RESOURCE =>
      Seq(
        FILTER(NOT(IsLiteral(obj)))
      )

    case ValueType(other) =>
      val subType = Br(  Pat(obj,RDF.TYPE,other)   )
      val subClass = Br (Pat(obj,RDFS.SUBCLASSOF,obj) )
      Seq(Union(subType,subClass))

    case ValueSet(set)=>
      Seq(
        FILTER(IN(obj,set.map(v=>v:RDFElement)))
      )
  }

  def arcQuery(arc:ArcRule,sub:Variable = Variable("sub"),pred:Variable = ?("pred"),obj:Variable = ?("obj")):SelectQuery = {

    val namePattern = this.arcNamePatterns(arc,sub,pred,obj)
    val valuePattern = this.arcValuePatterns(arc,sub,obj)
    val arcPatterns = namePattern++valuePattern
    SELECT (sub,pred,obj) WHERE(arcPatterns:_*)
  }



}
