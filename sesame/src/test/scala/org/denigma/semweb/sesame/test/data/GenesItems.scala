package org.denigma.semweb.sesame.test.data

import org.denigma.schemas.common.{Items, ItemsMock}
import org.denigma.schemas.genes.LoadGenAge
import org.denigma.semweb.rdf.IRI
import org.denigma.semweb.sesame._
import org.denigma.semweb.sesame.test.classes.Ontology
import org.denigma.semweb.shex._


object GenesItems extends LoadGenAge with ItemsMock{

  lazy val genes:List[PropertyModel] = this.loadData//.take(10)

  val cls: Seq[IRI] = Ontology.classes.map(cl=>cl:IRI).toSeq

  def populate(holder:Items)  = {
    holder.properties = this.properties
    holder.items = holder.items + (evidenceShape.id.asResource->genes)
    holder.shapes = holder.shapes + (evidenceShape.id.asResource-> evidenceShape)
  }

}