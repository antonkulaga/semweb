package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.{Res, IRI, CanBeObject, RDFElement}



case class EqualsFilter(left:Variable,right:Any) extends FilterElement
{
  def stringValue = left.stringValue+" = "+right.toString

}

trait FilterElement extends RDFElement

case class FILTER(elements:FilterElement*) extends GP
{
  lazy val children = elements.toList

  override def stringValue: String = if(hasChildren)  s"\nFILTER $foldChildren" else ""

}

case class NOT_EQUALS(one:RDFElement,two:RDFElement) extends FilterElement {

  override def stringValue: String = s"(${one.stringValue} != ${two.stringValue})"

}

case class EQUALS(one:RDFElement,two:RDFElement) extends FilterElement {

  override def stringValue: String = s"(${one.stringValue} = ${two.stringValue})"

}


case class IN(element:SelectElement,set:Set[RDFElement]) extends FilterElement
{
  override def stringValue = "("+element.toString+" IN ("+set.foldLeft("")( (acc,el)=>acc+","+el.toString).tail+") "+")"
}

case class NOT(element:FilterElement) extends FilterElement{

  def stringValue = element.stringValue match {
    case str if str.contains("=")=> str.replace("=","!=")
    case str => s"(!($str))"
  }
}

case class EXISTS(gr:GP) extends FilterElement
{
  def stringValue = s"EXISTS {${gr.toString}}"
}

case class IsIRI(v:Variable) extends FilterElement {
  def stringValue = s"isIRI(${v.toString})"
}


case class IsBlank(v:Variable) extends FilterElement {
  def stringValue = s"isBlank(${v.toString})"
}

case class IsLiteral(v:Variable) extends FilterElement {
  def stringValue = s"isLiteral(${v.toString})"
}

case class IsNumeric(v:Variable) extends FilterElement {
  def stringValue = s"isNumeric(${v.toString})"
}

case class DATATYPE(v:Variable,tp:Res) extends FilterElement {
  override def stringValue =s"( datatype(${v.toString}) = ${tp.toString} )"
  
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
