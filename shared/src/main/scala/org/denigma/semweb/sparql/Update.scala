package org.denigma.semweb.sparql

import org.denigma.semweb.rdf._


case class InsertQuery(insert:Insert)
case class DeleteQuery(delete:Delete)
case class InsertDeleteQuery(insert:Insert,delete:Delete)
case class DeleteInsertQuery(delete:Delete,insert:Insert)

case class InsertOnlyIf(insert:Insert,question:AskQuery)
case class DeleteOnlyIf(delete:Delete,question:AskQuery)
case class InsertDeleteOnlyIf(insert:Insert,delete:Delete,question:AskQuery)
case class DeleteInsertOnlyIf(delete:Delete,insert:Insert,question:AskQuery)

case class InsertUnless(insert:Insert,question:AskQuery)
case class DeleteUnless(delete:Delete,question:AskQuery)
case class InsertDeleteUnless(insert:Insert,delete:Delete,question:AskQuery)
case class DeleteInsertUnless(delete:Delete,insert:Insert,question:AskQuery)


class Insert(var children: List[RDFElement],context:Option[IRI] = None) extends GP with WithWhere
{

  lazy val hasDATA = children.exists(_.isInstanceOf[Data])

  lazy val into = context.map(c=>s"INTO ${c.toString}").getOrElse("")

  override def stringValue: String = if(hasDATA) s"INSERT $into ${this.foldChildren} "
    else s"INSERT $into  \n{ ${this.foldChildren} } "+ WHERE.stringValue

}

object INSERT
{

  //def INTO(iri:IRI)(elements: RDFElement*) =   new Insert(elements.toList ,Some(iri))

  def apply(data:Data): Insert = new Insert(data::Nil)

  def apply(graph:PatternGraph): Insert = new Insert(graph::Nil)
}



/**
 * Class to Delete values
 */
class Delete(var children: List[RDFElement]) extends GP with WithWhere {

  lazy val hasDATA = children.exists(_.isInstanceOf[Data])

  override def stringValue: String = if(hasDATA) s"DELETE${this.foldChildren} "
  else s"DELETE \n{ ${this.foldChildren} } "+ WHERE.stringValue

}

object DELETE {
  def apply(data:Data): Delete = {
    new Delete(data::Nil)
  }

  def apply(graph:PatternGraph): Delete = {
    new Delete(graph::Nil)
  }
}


object DATA {

  def INTO(iri:IRI)(triplets:Trip*) =   new Data(new TripletGraph(iri)(triplets.toList)::Nil)//new Data(triplets.toList,Some(iri))

  def apply(triplets:Trip*) = {
    new Data(triplets.toList)

  }

  def apply(graph:TripletGraph) = new Data(graph::Nil)

}

object GRAPH {

  def apply(id:CanBePredicate,triplets:List[Trip]) = new TripletGraph(id)(triplets)
  def apply(id:CanBePredicate,triplets:Trip*) = new TripletGraph(id)(triplets.toList)
  def apply(id:CanBePredicate,patterns:TripletPattern*) = new PatternGraph(id)(patterns.toList)
}



class Data(val children:List[RDFElement],context:Option[IRI] = None) extends GP {


  lazy val into = context.map(c=>s"INTO ${c.toString}").getOrElse("")

  override def stringValue: String = s"DATA $into \n{ ${this.foldChildren} }\n"


}

