package org.denigma.semweb.sparql

import org.denigma.semweb.rdf.{RDFValue, CanBeObject, IRI, RDFElement}

trait WithWhere extends RDFElement
{
  self=>

  object WHERE extends WhereClause(List.empty)
  {
    where=>

    def apply(elements:RDFElement*):self.type = {
      this.children = elements.toList
      self
    }

    def apply(elements:Set[RDFElement]):self.type = {
      this.children = elements.toList
      self
    }

  }


  def hasWhere = this.WHERE.hasChildren


}
object VALUES {
  def apply(children:Variable*) = Values(children,Seq.empty)
  
}

case class Values(children:Seq[Variable],values:Seq[Seq[RDFValue]]) extends RDFElement
{
  
  def apply(vals:RDFValue*) = copy(values = values :+ vals)
  
  def onlyOneChild = children.size ==1

  def foldChildren: String = children.foldLeft("")((acc,el)=>acc+" "+el.stringValue)
  

  override def stringValue = s"VALUES( ${this.foldChildren} )" + values.map(v=>v.mkString("("," ",")")).mkString("{\n",s"\n","}\n")
    s"\n}"

}

case class BIND(element:CanBeObject,variable:Variable) extends RDFElement{
  override def stringValue = s"BIND( ${element.toString} AS ${variable.toString} )"
  //BIND( <<?bob foaf:age ?age>> AS ?t ) .
}


/*
case class FROM(iri:IRI) extends DataSource{
  def stringValue = s"FROM ${iri.toString}"
}
case class FROM_NAMED(iri:IRI) extends DataSource{

  def stringValue = s"FROM NAMED ${iri.toString}"

}*/

trait  DataSource extends RDFElement

class WhereClause(elements:List[RDFElement]) extends Clause[RDFElement]("WHERE",elements)


class Clause[T](name:String,var children:List[RDFElement]) extends GP
{
  override def stringValue: String = if(!this.hasChildren) "" else s"$name \n{ ${this.foldChildren} }\n"
}


