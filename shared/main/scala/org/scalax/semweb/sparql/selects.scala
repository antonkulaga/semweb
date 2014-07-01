package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.RDFElement


object SELECT
{

  def apply(params:SelectElement*) = new SelectQuery(params.toList)
}

class SelectQuery(val params:List[SelectElement]) extends WithWhere with Sliced
{
  lazy val vars: Map[String, Variable] = params.collect{case v:Variable=>v.name->v}.toMap

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
  object LIMIT extends RDFElement
  {
    var limit:Long = Long.MaxValue
    def apply(limitValue:Long):self.type = {
      this.limit = limitValue
      self
    }

    def hasLimit:Boolean = this.limit!=Long.MaxValue && limit>0


    override def stringValue = if(hasLimit) s" LIMIT $limit" else ""
  }

  object OFFSET extends RDFElement
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
