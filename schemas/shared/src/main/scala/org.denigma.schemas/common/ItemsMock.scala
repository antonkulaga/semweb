package org.denigma.schemas.common

import org.denigma.semweb.rdf.{IRI, vocabulary}
import org.denigma.semweb.shex.ShapeBuilder

trait ItemsMock
{
  self:BasicSchema=>
  protected var properties = List.empty[IRI]

  def addProperty(iri:IRI): IRI = {
    properties = iri::properties
    iri
  }

  def addProperty(label:String): IRI = this.addProperty(de / label)

  protected val dc = IRI(vocabulary.DCElements.namespace)
  protected val pmid = de / "Pubmed"


  protected val rep = new ShapeBuilder(de / "Research_Support")


  //protected val pmid = addProperty(  IRI("http://denigma.org/resource/Pubmed/") )

  protected val article = addProperty( de /"Article")
  protected val authors =   addProperty(  de / "is_authored_by")
  protected val abs =addProperty( de / "abstract")
  protected val published =addProperty( de / "is_published_in")
  protected val title = addProperty(de / "title")
  protected val excerpt = addProperty(de / "excerpt")


}
