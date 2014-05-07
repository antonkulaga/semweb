package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.RDFElement


object CONSTRUCT {

  def apply(triplets:TripletPattern*) = new ConstructRDF()

}



class ConstructRDF(triplets:TripletPattern*) extends  WithWhere with GP{
  //TODO implement
  override def stringValue: String = s"CONSTRUCT"

  override def children: List[RDFElement] = triplets.toList
}

