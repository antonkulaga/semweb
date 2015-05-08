package org.denigma.semweb.sparql

import org.denigma.semweb.rdf._


case class INSERTQuery(insert:INSERT)
case class DELETEQuery(delete:DELETE)
case class INSERTDELETEQuery(insert:INSERT,delete:DELETE)
case class DELETEINSERTQuery(delete:DELETE,insert:INSERT)

case class INSERTOnlyIf(insert:INSERT,question:AskQuery)
case class DELETEOnlyIf(delete:DELETE,question:AskQuery)
case class INSERTDELETEOnlyIf(insert:INSERT,delete:DELETE,question:AskQuery)
case class DELETEINSERTOnlyIf(delete:DELETE,insert:INSERT,question:AskQuery)

case class INSERTUnless(insert:INSERT,question:AskQuery)
case class DELETEUnless(delete:DELETE,question:AskQuery)
case class INSERTDELETEUnless(insert:INSERT,delete:DELETE,question:AskQuery)
case class DELETEINSERTUnless(delete:DELETE,insert:INSERT,question:AskQuery)


class INSERT(var children: List[RDFElement],context:Option[IRI] = None) extends GP with WithWhere
{

  lazy val hasDATA = children.exists(_.isInstanceOf[DATA])

  lazy val into = context.map(c=>s"INTO ${c.toString}").getOrElse("")

  override def stringValue: String = if(hasDATA) s"INSERT $into ${this.foldChildren} "
    else s"INSERT $into  \n{ ${this.foldChildren} } "+ WHERE.stringValue

}

object INSERT
{

  //def INTO(iri:IRI)(elements: RDFElement*) =   new INSERT(elements.toList ,Some(iri))

  def apply(data:DATA): INSERT = new INSERT(data::Nil)

  def apply(graph:PatternGraph): INSERT = new INSERT(graph::Nil)
}



/**
 * Class to DELETE values
 */
class DELETE(var children: List[RDFElement]) extends GP with WithWhere {

  lazy val hasDATA = children.exists(_.isInstanceOf[DATA])

  override def stringValue: String = if(hasDATA) s"DELETE${this.foldChildren} "
  else s"DELETE \n{ ${this.foldChildren} } "+ WHERE.stringValue

}

object DELETE {
  def apply(data:DATA): DELETE = {
    new DELETE(data::Nil)
  }

  def apply(graph:PatternGraph): DELETE = {
    new DELETE(graph::Nil)
  }
}


object DATA {

  def INTO(iri:IRI)(triplets:Trip*) =   new DATA(new TripletGraph(iri)(triplets.toList)::Nil)//new Data(triplets.toList,Some(iri))

  def apply(triplets:Trip*) = {
    new DATA(triplets.toList)

  }

  def apply(graph:TripletGraph) = new DATA(graph::Nil)

}

object GRAPH {

  def apply(id:CanBePredicate,triplets:List[Trip]) = new TripletGraph(id)(triplets)
  def apply(id:CanBePredicate,triplets:Trip*) = new TripletGraph(id)(triplets.toList)
  def apply(id:CanBePredicate,patterns:TripletPattern*) = new PatternGraph(id)(patterns.toList)
}



class DATA(val children:List[RDFElement],context:Option[IRI] = None) extends GP {


  lazy val into = context.map(c=>s"INTO ${c.toString}").getOrElse("")

  override def stringValue: String = s"DATA $into \n{ ${this.foldChildren} }\n"


}

