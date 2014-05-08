package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.{Trip, RDFElement, CanBePredicate}


case class InsertQuery(insert:Insert)
case class DeleteQuery(delete:Delete)
case class InsertDeleteQuery(insert:Insert,delete:Delete)
case class DeleteInsertQuery(delete:Delete,insert:Insert)

case class InsertOnlyIf(insert:Insert,question:AskRDF)
case class DeleteOnlyIf(delete:Delete,question:AskRDF)
case class InsertDeleteOnlyIf(insert:Insert,delete:Delete,question:AskRDF)
case class DeleteInsertOnlyIf(delete:Delete,insert:Insert,question:AskRDF)

case class InsertUnless(insert:Insert,question:AskRDF)
case class DeleteUnless(delete:Delete,question:AskRDF)
case class InsertDeleteUnless(insert:Insert,delete:Delete,question:AskRDF)
case class DeleteInsertUnless(delete:Delete,insert:Insert,question:AskRDF)


class Insert(var children: List[RDFElement]) extends GP with WithWhere
{

  lazy val hasDATA = children.exists(_.isInstanceOf[Data])

  override def stringValue: String = if(hasDATA) s"INSERT ${this.foldChildren} "
    else s"INSERT \n{ ${this.foldChildren} } "+ WHERE.stringValue

}

object INSERT
{
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



class Data(val children:List[RDFElement]) extends GP {


  override def stringValue: String = s"DATA \n{ ${this.foldChildren} }\n"


}

