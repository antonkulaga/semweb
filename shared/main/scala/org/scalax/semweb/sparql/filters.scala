package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.QueryElement


trait Filtered {
  self=>

  object FILTER extends  GP
  {
    var children = List.empty[QueryElement]

    val parent:self.type = self

    def apply(f:Filter) = parent

    override def stringValue: String = if(hasChildren)  "FILTER "+foldChildren else ""

  }
}

trait HavingContainer
{
  self=>


  object HAVING  extends GP
  {
    var children = List.empty[QueryElement]
    val parent:self.type = self

    def apply() = parent

    //TODO finish
    override def stringValue: String = if(hasChildren)  "HAVING "+foldChildren else ""
  }
}


case class Min(v:Variable) extends Aggregate(v) {

  override def stringValue = s"MIN(${v.stringValue})"
}

case class Max(v:Variable)extends Aggregate(v) {

  override def stringValue = s"MAX(${v.stringValue})"
}

case class Sum(v:Variable)extends Aggregate(v) {

  override def stringValue = s"SUM(${v.stringValue})"
}

case class Avg(v:Variable)extends Aggregate(v) {

  override def stringValue = s"AVG(${v.stringValue})"
}


case class Count(v:Variable)extends Aggregate(v) {

  override def stringValue = s"COUNT(${v.stringValue})"
}

/**
 * Aggregator class
 * @param variable variable that should be aggregated
 */
abstract class Aggregate(variable:Variable) extends QueryElement{

}
