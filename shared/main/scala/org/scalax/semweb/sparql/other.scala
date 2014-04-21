package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.{QueryElement, IRIPatEl}


trait SelectElement extends QueryElement
{
  def isVar:Boolean = false
  def isAgg:Boolean = false
}


case class Variable(name:String) extends IRIPatEl with SelectElement  {
  override def toString = stringValue

  def stringValue = s"?$name"



  override def isVar = true

}
case class EqualsFilter(left:Variable,right:Any) extends Filter {
  override def toString = left.toString+" = "+right.toString
}
class Filter extends QueryElement
{
  def stringValue = "FILTER"
}


trait TreeElement[T] {
  def parent:T
}