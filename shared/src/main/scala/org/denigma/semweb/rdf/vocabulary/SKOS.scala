package org.denigma.semweb.rdf.vocabulary

import org.denigma.semweb.rdf.IRI


object SKOS {
  val namespace: String = "http://www.w3.org/2004/02/skos/core#"
  val skos = IRI(namespace)
  
  val Collection: IRI = skos / "Collection"
  val Concept: IRI = skos / "Concept"
  val ConceptScheme: IRI = skos / "ConceptScheme"
  val OrderedCollection: IRI = skos / "OrderedCollection"
  val altLabel: IRI = skos / "altLabel"
  val broadMatch: IRI = skos / "broadMatch"
  val broader: IRI = skos / "broader"
  val broaderTransitive: IRI = skos / "broaderTransitive"
  val changeNote: IRI = skos / "changeNote"
  val closeMatch: IRI = skos / "closeMatch"
  val definition: IRI = skos / "definition"
  val editorialNote: IRI = skos / "editorialNote"
  val exactMatch: IRI = skos / "exactMatch"
  val example: IRI = skos / "example"
  val hasTopConcept: IRI = skos / "hasTopConcept"
  val hiddenLabel: IRI = skos / "hiddenLabel"
  val historyNote: IRI = skos / "historyNote"
  val inScheme: IRI = skos / "inScheme"
  val mappingRelation: IRI = skos / "mappingRelation"
  val member: IRI = skos / "member"
  val memberList: IRI = skos / "memberList"
  val narrowMatch: IRI = skos / "narrowMatch"
  val narrow: IRI = skos / "narrow"
  val narrowTransitive: IRI = skos / "narrowTransitive"
  val notation: IRI = skos / "notation"
  val note: IRI = skos / "note"
  val prefLabel: IRI = skos / "prefLabel"
  val related: IRI = skos / "related"
  val relatedMatch: IRI = skos / "relatedMatch"
  val scopeNote: IRI = skos / "scopeNote"
  val semanticRelation: IRI = skos / "semanticRelation"
  val topConceptOf: IRI = skos / "topConceptOf"

}
