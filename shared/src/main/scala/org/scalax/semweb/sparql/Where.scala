package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.{CanBeObject, IRI, RDFElement}

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

  }


  def hasWhere = this.WHERE.hasChildren


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


