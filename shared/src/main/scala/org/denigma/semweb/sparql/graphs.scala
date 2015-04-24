package org.denigma.semweb.sparql

import org.denigma.semweb.rdf.{Trip, RDFElement, CanBePredicate}

/**
 * grph that may contains only triplets (used by INSERT/DELETE DATA)
 * @param id id of the Graph
 * @param triplets list of triplets to be added
 */
class TripletGraph(val id:CanBePredicate)(val triplets:List[Trip]) extends SPARQLGraph{

  type Triplet = Trip

}

class PatternGraph(val id:CanBePredicate)(val triplets:List[TripletPattern]) extends SPARQLGraph
{

  type Triplet = TripletPattern

  override def children: List[RDFElement] = triplets.toList
}

trait SPARQLGraph extends GP{

  def id:CanBePredicate

  type Triplet<:RDFElement

  val triplets:List[Triplet]

  override def stringValue: String = id match {
    case v:Variable=> s" GRAPH <${v.name}> \n{ ${this.foldChildren} } "
    case other=> s" GRAPH <${other.stringValue}> \n{ ${this.foldChildren} } "
  }

  override def children: List[RDFElement] = triplets.toList


}
