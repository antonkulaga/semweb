package org.denigma.semweb.sparql

import org.denigma.semweb.rdf.{Res, IRI, CanBeObject, RDFElement}



case class EqualsFilter(left:Variable,right:Any) extends FilterElement
{
  def stringValue = left.stringValue+" = "+right.toString

}

trait FilterElement extends RDFElement

case class FILTER(elements:FilterElement*) extends GP
{
  lazy val children = elements.toList

  //def foldChildren: String = children.foldLeft("")((acc,el)=>acc+" "+el.stringValue)

  override def stringValue: String = if(hasChildren)  s"\nFILTER($foldChildren)" else ""

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

case class IsIRI(v:CanBeObject) extends FilterElement {
  def stringValue = s"isIRI(${v.toString})"
}


case class IsBlank(v:CanBeObject) extends FilterElement {
  def stringValue = s"isBlank(${v.toString})"
}

case class IsLiteral(v:CanBeObject) extends FilterElement {
  def stringValue = s"isLiteral(${v.toString})"
}

case class IsNumeric(v:CanBeObject) extends FilterElement {
  def stringValue = s"isNumeric(${v.toString})"
}

case class DATATYPE(v:CanBeObject,tp:Res) extends FilterElement {
  override def stringValue =s"( datatype(${v.toString}) = ${tp.toString} )"
  
}
object STR_STARTS{
  def apply(o:CanBeObject,substring:String):STR_STARTS = STR_STARTS(STR(o),substring)
}
case class STR_STARTS(str:STR, start:String) extends FilterElement {
  override def stringValue: String = s"""STRSTARTS(${str.stringValue}, "$start")"""
}

object STR_CONTAINS{
  def apply(o:CanBeObject,substring:String):STR_CONTAINS = STR_CONTAINS(STR(o),substring)
}
case class STR_CONTAINS(str:STR, substring:String) extends FilterElement {
  override def stringValue: String = s"""CONTAINS(${str.stringValue}, "$substring")"""
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
