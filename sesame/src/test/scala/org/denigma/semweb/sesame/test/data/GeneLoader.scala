package org.denigma.semweb.sesame.test.data

import java.io.InputStream

import org.denigma.schemas.common.Write2File
import org.denigma.semweb.sesame.test.classes.BigData

/**
 * Loads genes resources into database
 */
trait GeneLoader extends Write2File{

  def loadData(db:BigData) = {
    val st: InputStream = getClass.getResourceAsStream("/genes_shape.ttl")
    db.parseStream("gene_shapes.ttl",st)
    val dt: InputStream = getClass.getResourceAsStream("/genes_data.ttl")
    db.parseStream("genes_data.ttl",dt)
    val ont: InputStream = getClass.getResourceAsStream("/ontology.ttl")
    db.parseStream("ontology.ttl",ont)
    val web: InputStream = getClass.getResourceAsStream("/gero.longevityalliance.org.ttl")
    db.parseStream("gero.longevityalliance.org.ttl",web)
  }


}