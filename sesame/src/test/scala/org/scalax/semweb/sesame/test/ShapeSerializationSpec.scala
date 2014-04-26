package org.scalax.semweb.sesame.test

import org.scalatest.{Matchers, WordSpec}
import scala.util.Try
import org.openrdf.repository.RepositoryResult
import org.openrdf.model.Statement

/**
 *
 */
class ShapeSerializationSpec extends  WordSpec with Matchers {
  self=> //alias to this, to avoid confusion

  "RDF shapes" should {

    "Provide errors for wrong queries" in {

      val db = BigData(true) //cleaning the files and initializing the database


      //UNCOMMENT FOLLOWING LINES TO SEE TIMEOUTS
      //val queryFreeze= db.select(wrongQuery)
      db.shutDown() // shutting down

    }
  }



}
