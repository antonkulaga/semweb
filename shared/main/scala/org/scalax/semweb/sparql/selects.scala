package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.QueryElement


object SELECT
{

  def apply(params:SelectElement*) = new SelectQuery(params.toList)
}

class SelectQuery(val params:List[SelectElement]) extends WithWhere with VarContainer with Sliced
{
  self=>
  object DISTINCT {
    //TODO: complete
  }

  def stringValue = s"SELECT ${params.foldLeft("")( (acc,el)=>acc+" "+el.stringValue)} \n${WHERE.stringValue}\n ${LIMIT.stringValue} ${OFFSET.stringValue}"

}

trait Sliced {
  self=>

  /**
   * Limit object
   */
  object LIMIT extends QueryElement
  {
    var limit:Long = Long.MaxValue
    def apply(limitValue:Long):self.type = {
      this.limit = limitValue
      self
    }

    def hasLimit:Boolean = this.limit!=Long.MaxValue && limit>0


    override def stringValue = if(hasLimit) s" LIMIT $limit" else ""
  }

  object OFFSET extends QueryElement
  {
    var offset:Long = 0
    def apply(offsetValue:Long):self.type  = {
      this.offset = offsetValue
      self
    }

    def hasOffset:Boolean = this.offset>0


    override def stringValue = if(this.hasOffset) s" OFFSET $offset" else ""
  }
}
