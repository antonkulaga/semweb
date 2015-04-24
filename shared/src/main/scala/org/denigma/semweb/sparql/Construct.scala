package org.denigma.semweb.sparql

import org.denigma.semweb.rdf.RDFElement


object CONSTRUCT {

  def apply(triplets:TripletPattern*) = new ConstructQuery(triplets:_*)
  
  lazy val empty = new ConstructQuery()

}



class ConstructQuery(triplets:TripletPattern*) extends  WithWhere with GP{
  
 def stringValue = s"CONSTRUCT { ${this.foldChildren} } \n${WHERE.stringValue}\n"
  //s"SELECT $selection $from\n${WHERE.stringValue}\n ${LIMIT.stringValue} ${OFFSET.stringValue}"

  override def children: List[RDFElement] = triplets.toList
  
  def isEmpty = this.triplets.size==0

  def isDefined = this.triplets.size!=0

}

