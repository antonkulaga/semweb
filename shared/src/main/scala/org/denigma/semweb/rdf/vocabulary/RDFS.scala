package org.denigma.semweb.rdf.vocabulary

import org.denigma.semweb.rdf.IRI


/**
 * Constants for RDF Schema primitives and for the RDF Schema namespace.
 */
object RDFS {

  val namespace: String = "http://www.w3.org/2000/01/rdf-schema#"
  val rdfs = IRI(namespace)

  /** http://www.w3.org/2000/01/rdf-schema# */
   val NAMESPACE: String = "http://www.w3.org/2000/01/rdf-schema#"
  /** http://www.w3.org/2000/01/rdf-schema#Resource */
   val RESOURCE: IRI = rdfs / "Resource"
  /** http://www.w3.org/2000/01/rdf-schema#Literal */
   val LITERAL: IRI = rdfs / "Literal"
  /** http://www.w3.org/2000/01/rdf-schema#Class */
   val CLASS: IRI = rdfs / "Class"
  /** http://www.w3.org/2000/01/rdf-schema#subClassOf */
   val SUBCLASSOF: IRI = rdfs / "subClassOf"
  /** http://www.w3.org/2000/01/rdf-schema#subPropertyOf */
   val SUBPROPERTYOF: IRI = rdfs / "subPropertyOf"
  /** http://www.w3.org/2000/01/rdf-schema#domain */
   val DOMAIN: IRI = rdfs / "domain"
  /** http://www.w3.org/2000/01/rdf-schema#range */
   val RANGE: IRI = rdfs / "range"
  /** http://www.w3.org/2000/01/rdf-schema#comment */
   val COMMENT: IRI = rdfs / "comment"
  /** http://www.w3.org/2000/01/rdf-schema#label */
   val LABEL: IRI = rdfs / "label"
  /** http://www.w3.org/2000/01/rdf-schema#Datatype */
   val DATATYPE: IRI = rdfs / "Datatype"
  /** http://www.w3.org/2000/01/rdf-schema#Container */
   val CONTAINER: IRI = rdfs / "Container"
  /** http://www.w3.org/2000/01/rdf-schema#member */
   val MEMBER: IRI = rdfs / "member"
  /** http://www.w3.org/2000/01/rdf-schema#isDefinedBy */
   val ISDEFINEDBY: IRI = rdfs / "isDefinedBy"
  /** http://www.w3.org/2000/01/rdf-schema#seeAlso */
   val SEEALSO: IRI = rdfs / "seeAlso"
  /** http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty */
   val CONTAINERMEMBERSHIPPROPERTY: IRI = rdfs / "ContainerMembershipProperty"
}


