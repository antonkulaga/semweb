package org.scalax.semweb.sesame.test

import scala.util.Try
import org.openrdf.query.BindingSet


import org.scalatest._


import scala.collection.immutable.List
import org.openrdf.model.{Statement, Value}
import org.openrdf.repository.RepositoryResult
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import org.scalax.semweb.sparql._
import org.scalax.semweb.rdf.IRI

/**
 * Tests SPARQL DSL on bigdata
 */
class BigDataSparqlSpec  extends  WordSpec with Matchers with SimpleTestData {
  self=> //alias to this, to avoid confusion

  "Bigdata spec" should {

    "Provide errors for wrong queries" in {

      val db = BigData(true) //cleaning the files and initializing the database
      self.addTestData(db) //add test data ( see SimpleTestData )

      val wrongQuery =
      """
        | SELECT ?subject ?property ?object WHERE
        | {
        | ?subject ?property ?object .
        | FILTER( STR(?property) "lov*") .
        | }
        | LIMIT 50
        | """
        .stripMargin('|')

      //UNCOMMENT FOLLOWING LINES TO SEE TIMEOUTS
     val queryFreeze= db.select(wrongQuery)
     queryFreeze.isFailure shouldBe true
     db.shutDown() // shutting down

    }


    "read SELECT quries successfully" in {

      val db = BigData(true) //cleaning the files and initializing the database

      self.addTestData(db) //add test data ( see SimpleTestData )


      val tryLove:Try[RepositoryResult[Statement]]= db.read{con=>
        con.getStatements(null,self.loves,null,true)
      }
      tryLove.isSuccess shouldBe true

      val resLove = tryLove.get.toList
      resLove.size shouldEqual 6

      //"SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"
      val query =  SELECT(?("s"), ?("o") ) WHERE Pat(?("s"), IRI("http://denigma.org/relations/resources/loves"), ?("o"))
      val queryLove= db.select(query.stringValue)
      queryLove.isSuccess shouldBe  true
      queryLove.get.toList.size shouldEqual 6

      db.shutDown() // shutting down
    }

  }
  
  
  
}
