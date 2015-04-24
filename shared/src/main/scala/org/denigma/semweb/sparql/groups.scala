package org.denigma.semweb.sparql

import org.denigma.semweb.rdf.RDFElement


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
 * Just a trait that can wrao elements with {} 
 */
trait Wrapper extends GP{
  def wrap(el:RDFElement): String = el match {
    case gp:GP=> gp.stringValue
    case other => s"{${other.stringValue}}"

  }

}


/**
unites to groups together
 */
case class Union(left:RDFElement,right:RDFElement) extends Wrapper {
  

  override def stringValue = s"${wrap(left)} UNION ${wrap(right)}"

  override val children = left::right::Nil


}

case class Optional(el:RDFElement*) extends GP
{

  def stringValue = s"\n OPTIONAL {${this.foldChildren}}"

  override lazy val children = el.toList

}



//trait VarContainer extends RDFElement{
//
// var vars = Map.empty[String,Variable]
//
//}
