package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.{CanBeObject, CanBeSubject, RDFElement, CanBePredicate}


trait SelectElement extends RDFElement
{
  def isVar:Boolean = false
  def isAgg:Boolean = false
}

case class RDR(  sub:CanBeSubject, pred:CanBePredicate, obj:CanBeObject) extends TripletPattern with CanBeSubject{

  override def stringValue: String = s"<<${sub.toString} ${pred.toString} ${obj.toString}>>"

  override def toString = stringValue
}


case class Variable(name:String) extends CanBePredicate with SelectElement  {
  override def toString = stringValue

  def stringValue = s"?$name"

  override def isVar = true

}


trait TreeElement[T] {
  def parent:T
}