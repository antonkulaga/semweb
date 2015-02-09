package org.scalax.semweb.sparql

import org.scalax.semweb.rdf.RDFElement


object CONSTRUCT {

  def apply(triplets:TripletPattern*) = new ConstructQuery(triplets:_*)

}



class ConstructQuery(triplets:TripletPattern*) extends  WithWhere with GP{
  
 def stringValue = s"CONSTRUCT { ${this.foldChildren} } \n${WHERE.stringValue}\n"
  //s"SELECT $selection $from\n${WHERE.stringValue}\n ${LIMIT.stringValue} ${OFFSET.stringValue}"

  override def children: List[RDFElement] = triplets.toList
}

