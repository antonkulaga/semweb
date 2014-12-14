package org.scalax.semweb.rdf.vocabulary

import org.scalax.semweb.rdf.IRI

/**
 * Constants for OWL primitives and for the OWL namespace.
 */
object OWL {


  val namespace: String = "http://www.w3.org/2002/07/owl#"
  val owl = IRI(namespace)
  
  /** http://www.w3.org/2002/07/owl# */
   val NAMESPACE: String = "http://www.w3.org/2002/07/owl#"
  /** http://www.w3.org/2002/07/owl#Class */
   val CLASS: IRI = owl / "Class"
  /** http://www.w3.org/2002/07/owl#Individual */
   val INDIVIDUAL: IRI = owl / "Individual"
  /** http://www.w3.org/2002/07/owl#Thing */
   val THING: IRI = owl / "Thing"
  /** http://www.w3.org/2002/07/owl#Nothing */
   val NOTHING: IRI = owl / "Nothing"
  /** http://www.w3.org/2002/07/owl#equivalentClass */
   val EQUIVALENTCLASS: IRI = owl / "equivalentClass"
  /** http://www.w3.org/2002/07/owl#equivalentProperty */
   val EQUIVALENTPROPERTY: IRI = owl / "equivalentProperty"
  /** http://www.w3.org/2002/07/owl#sameAs */
   val SAMEAS: IRI = owl / "sameAs"
  /** http://www.w3.org/2002/07/owl#differentFrom */
   val DIFFERENTFROM: IRI = owl / "differentFrom"
  /** http://www.w3.org/2002/07/owl#AllDifferent */
   val ALLDIFFERENT: IRI = owl / "AllDifferent"
  /** http://www.w3.org/2002/07/owl#distinctMembers */
   val DISTINCTMEMBERS: IRI = owl / "distinctMembers"
  /** http://www.w3.org/2002/07/owl#ObjectProperty */
   val OBJECTPROPERTY: IRI = owl / "ObjectProperty"
  /** http://www.w3.org/2002/07/owl#DatatypeProperty */
   val DATATYPEPROPERTY: IRI = owl / "DatatypeProperty"
  /** http://www.w3.org/2002/07/owl#inverseOf */
   val INVERSEOF: IRI = owl / "inverseOf"
  /** http://www.w3.org/2002/07/owl#TransitiveProperty */
   val TRANSITIVEPROPERTY: IRI = owl / "TransitiveProperty"
  /** http://www.w3.org/2002/07/owl#SymmetricProperty */
   val SYMMETRICPROPERTY: IRI = owl / "SymmetricProperty"
  /** http://www.w3.org/2002/07/owl#FunctionalProperty */
   val FUNCTIONALPROPERTY: IRI = owl / "FunctionalProperty"
  /** http://www.w3.org/2002/07/owl#InverseFunctionalProperty */
   val INVERSEFUNCTIONALPROPERTY: IRI = owl / "InverseFunctionalProperty"
  /** http://www.w3.org/2002/07/owl#Restriction */
   val RESTRICTION: IRI = owl / "Restriction"
  /** http://www.w3.org/2002/07/owl#onProperty */
   val ONPROPERTY: IRI = owl / "onProperty"
  /** http://www.w3.org/2002/07/owl#allValuesFrom */
   val ALLVALUESFROM: IRI = owl / "allValuesFrom"
  /** http://www.w3.org/2002/07/owl#someValuesFrom */
   val SOMEVALUESFROM: IRI = owl / "someValuesFrom"
  /** http://www.w3.org/2002/07/owl#minCardinality */
   val MINCARDINALITY: IRI = owl / "minCardinality"
  /** http://www.w3.org/2002/07/owl#maxCardinality */
   val MAXCARDINALITY: IRI = owl / "maxCardinality"
  /** http://www.w3.org/2002/07/owl#cardinality */
   val CARDINALITY: IRI = owl / "cardinality"
  /** http://www.w3.org/2002/07/owl#Ontology */
   val ONTOLOGY: IRI = owl / "Ontology"
  /** http://www.w3.org/2002/07/owl#imports */
   val IMPORTS: IRI = owl / "imports"
  /** http://www.w3.org/2002/07/owl#intersectionOf */
   val INTERSECTIONOF: IRI = owl / "intersectionOf"
  /** http://www.w3.org/2002/07/owl#versionInfo */
   val VERSIONINFO: IRI = owl / "versionInfo"
  /** http://www.w3.org/2002/07/owl#priorVersion */
   val PRIORVERSION: IRI = owl / "priorVersion"
  /** http://www.w3.org/2002/07/owl#backwardCompatibleWith */
   val BACKWARDCOMPATIBLEWITH: IRI = owl / "backwardCompatibleWith" 
  /** http://www.w3.org/2002/07/owl#incompatibleWith */
   val INCOMPATIBLEWITH: IRI = owl / "incompatibleWith"
  /** http://www.w3.org/2002/07/owl#DeprecatedClass */
   val DEPRECATEDCLASS: IRI = owl / "DeprecatedClass"
  /** http://www.w3.org/2002/07/owl#DeprecatedProperty */
   val DEPRECATEDPROPERTY: IRI = owl / "DeprecatedProperty"
  /** http://www.w3.org/2002/07/owl#AnnotationProperty */
   val ANNOTATIONPROPERTY: IRI = owl / "AnnotationProperty"
  /** http://www.w3.org/2002/07/owl#OntologyProperty */
   val ONTOLOGYPROPERTY: IRI = owl / "OntologyProperty"
  /** http://www.w3.org/2002/07/owl#oneOf */
   val ONEOF: IRI = owl / "oneOf"
  /** http://www.w3.org/2002/07/owl#hasValue */
   val HASVALUE: IRI = owl / "hasValue"
  /** http://www.w3.org/2002/07/owl#disjointWith */
   val DISJOINTWITH: IRI = owl / "disjointWith"
  /** http://www.w3.org/2002/07/owl#unionOf */
   val UNIONOF: IRI = owl / "unionOf"
  /** http://www.w3.org/2002/07/owl#complementOf */
   val COMPLEMENTOF: IRI = owl / "complementOf"
}


