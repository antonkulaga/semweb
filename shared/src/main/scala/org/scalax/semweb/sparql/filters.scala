package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.{CanBeObject, RDFElement}



case class EqualsFilter(left:Variable,right:Any) extends FilterElement
{
  def stringValue = left.stringValue+" = "+right.toString

}

trait FilterElement extends RDFElement

case class FILTER(elements:FilterElement*) extends GP
{
  lazy val children = elements.toList

  override def stringValue: String = if(hasChildren)  "FILTER "+foldChildren else ""

}

case class EXISTS(gr:GP) extends FilterElement
{
  def stringValue = s"EXISTS {${gr.toString}}"
}


case class STR_STARTS(str:STR, start:String) extends FilterElement {
  override def stringValue: String = s"""STRSTARTS(str(${str.stringValue}), "$start")"""
}

case class STR_CONTAINS(str:STR, substring:String) extends FilterElement {
  override def stringValue: String = s"""STRCONTAINS(str(${str.stringValue}), "$substring")"""
}


case class STR(element: RDFElement) extends RDFElement {
  override def stringValue: String = s"STR(${element.stringValue})"
}

trait HavingContainer
{
  self=>


  object HAVING  extends GP
  {
    var children = List.empty[RDFElement]
    val parent:self.type = self

    def apply() = parent

    //TODO finish
    override def stringValue: String = if(hasChildren)  "HAVING "+foldChildren else ""
  }
}


case class Min(v:Variable) extends Aggregate(v) {

  override def stringValue = s"MIN(${v.stringValue})"
}

case class Max(v:Variable)extends Aggregate(v) {

  override def stringValue = s"MAX(${v.stringValue})"
}

case class Sum(v:Variable)extends Aggregate(v) {

  override def stringValue = s"SUM(${v.stringValue})"
}

case class Avg(v:Variable)extends Aggregate(v) {

  override def stringValue = s"AVG(${v.stringValue})"
}


case class Count(v:Variable)extends Aggregate(v) {

  override def stringValue = s"COUNT(${v.stringValue})"
}

/**
 * Aggregator class
 * @param variable variable that should be aggregated
 */
abstract class Aggregate(variable:Variable) extends RDFElement{

}
