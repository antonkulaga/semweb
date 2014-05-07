package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.RDFElement


/**
 * Sparql brackets
 * @param elements elements included
 */
case class Br(elements:RDFElement*) extends GP
{

  override def stringValue = "\n{"+this.foldChildren+" }"

  override lazy val children: List[RDFElement] = elements.toList
}

/**
Group of elements
 */
trait GP extends RDFElement{

  def children:List[RDFElement]

  def hasChildren = !children.isEmpty

  def onlyOneChild = children.size ==1

  def foldChildren: String = children.foldLeft("")((acc,el)=>acc+" "+el.stringValue)

  def UNION(other:GP):Union = Union(this,other)
}


/**
unites to groups together
 */
case class Union(left:RDFElement,right:RDFElement) extends GP {

  override def stringValue = s" ${left.stringValue} UNION ${right.stringValue}"

  override val children = left::right::Nil


}

case class Optional(gp:RDFElement) extends GP
{
  def stringValue = "\n OPTIONAL "+ gp.toString

  override val children = gp::Nil

}



trait VarContainer extends RDFElement{

 var vars = Map.empty[String,Variable]

}
