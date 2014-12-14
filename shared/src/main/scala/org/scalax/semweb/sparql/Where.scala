package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.RDFElement

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

class WhereClause(var children:List[RDFElement]) extends GP
{
  where=>

  override def stringValue: String = if(!this.hasChildren) "" else s"WHERE \n{ ${this.foldChildren} }\n"
}

