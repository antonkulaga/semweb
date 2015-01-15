package org.scalax.semweb.sparql

import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.IRI


/**
 * Basic graph pattern
 * @param sub Subject (Var,Literal or Resource)
 * @param pred Predicate (Var,Literal or Resource)
 * @param obj Object (Var,Literal or Resource)
 * @param cont Context (Var,Literal or Resource or Null)
 */
case class Pat(sub:CanBeSubject,pred:CanBePredicate,obj:CanBeObject,cont:CanBeSubject= null) extends QuadPattern



trait QuadPattern extends TripletPattern{
  def hasContext = cont!=null
  def cont:CanBeSubject
  override def stringValue: String = s"\n ${sub.toString} ${pred.toString} ${obj.toString}" + (if(hasContext) " "+cont.toString+" .\n" else " .\n")

  //def contextOrNull = if(this.hasContext) this.c.resourceOrNull else null

  def contextResOrNull:Res= cont match {
    case res:Res=>res
    case _=>null
  }

  def canBindContextRes(res:Res) = cont match {
    case v:Variable=>true
    case null=>true
    case r:Res if res!=null=>r.stringValue==res.stringValue
    case _=>false
  }

  def canBind(q:Quad) = super.canBindTriplet(q) && (!this.hasContext || this.canBindContextRes(q.cont))

}

trait TripletPattern extends RDFElement
{
  def sub:CanBeSubject
  def pred:CanBePredicate
  def obj:CanBeObject
  override def stringValue: String = s"\n ${sub.stringValue} ${pred.stringValue} ${obj.stringValue} .\n"

  def canBindSubjectRes(res:Res) = sub match {
    case v:Variable=>true
    case null=>true
    case r:Res if res!=null=>r.stringValue==res.stringValue
    case _=>false
  }


  def canBindPredicateIri(iri:IRI) = pred match {
    case v:Variable=>true
    case null=>true
    case r:IRI if iri!=null=>r.stringValue==iri.stringValue
    case _=>false
  }


  def canBindObjectValue(v:RDFValue) = obj match {
    case v:Variable=>true
    case null=>true
    case r:RDFValue if v!=null=>r.stringValue==v.stringValue
    case _=>false
  }

  def canBindTriplet(trip: BasicTriplet) = canBindSubjectRes(trip.sub) && canBindPredicateIri(trip.pred) && canBindObjectValue(trip.obj)

  def subjectResourceOrNull:Res =sub match {
    case res:Res=>res
    case _=>null
  }

  def predicateIRIOrNull:IRI = pred match {
    case iri:IRI=>iri
    case _=>null
  }

  def objectValueOrNull:RDFValue = obj match {
    case value:RDFValue=>value
    case _=>null
  }

}





