package org.denigma.semweb.sparql

import org.denigma.semweb.rdf.{IRI, RDFElement}


object SELECT
{
  //def apply(params:SelectElement*): SelectQuery = new SelectQuery(params = params.toList)

  def DISTINCT(params:SelectElement*): SelectQuery = SelectQuery(params)

  def apply(params:SelectElement*): SelectQuery = SelectQuery(params)
}

case object Distinct extends Modifier{
  override def stringValue: String = "DISTINCT"
}
trait Modifier extends RDFElement

case class SelectQuery(params:Seq[SelectElement],modifiers:Seq[RDFElement] = Seq.empty,from:String = "") extends WithWhere with Sliced
{
  lazy val vars: Map[String, Variable] = params.collect{case v:Variable=>v.name->v}.toMap

  protected lazy val selection = params.foldLeft("")( (acc,el)=>acc+" "+el.stringValue)

  def FROM(iri:IRI):SelectQuery =  this.copy(from= from + s"FROM ${iri.toString} ") //DIRTY but working

  def FROM_NAMED(iri:IRI):SelectQuery  =  this.copy(from= from + s"FROM NAMED ${iri.toString} ")


  def stringValue = s"SELECT ${modifiers.map(_.stringValue).mkString(" ")} $selection $from\n${WHERE.stringValue}\n ${LIMIT.stringValue} ${OFFSET.stringValue}"

}

trait Sliced {
  self=>

  /**
   * Limit object
   */
  object LIMIT extends RDFElement
  {
    var limit:Int = Int.MaxValue
    def apply(limitValue:Int):self.type = {
      this.limit = limitValue
      self
    }

    def hasLimit:Boolean = this.limit!=Int.MaxValue && limit>0


    override def stringValue = if(hasLimit) s" LIMIT $limit" else ""
  }

  object OFFSET extends RDFElement
  {
    var offset:Int = 0
    def apply(offsetValue:Int):self.type  = {
      this.offset = offsetValue
      self
    }

    def hasOffset:Boolean = this.offset>0


    override def stringValue = if(this.hasOffset) s" OFFSET $offset" else ""
  }
}
