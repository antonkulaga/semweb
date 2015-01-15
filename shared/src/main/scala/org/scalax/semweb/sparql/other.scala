package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.{RDFElement, CanBePredicate}


trait SelectElement extends RDFElement
{
  def isVar:Boolean = false
  def isAgg:Boolean = false
}


case class Variable(name:String) extends CanBePredicate with SelectElement  {
  override def toString = stringValue

  def stringValue = s"?$name"



  override def isVar = true

}


trait TreeElement[T] {
  def parent:T
}