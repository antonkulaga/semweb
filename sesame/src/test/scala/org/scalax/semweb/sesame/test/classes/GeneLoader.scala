package org.scalax.semweb.sesame.test.classes

import java.io.InputStream

/**
 * Loads genes resources into database
 */
trait GeneLoader {

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
