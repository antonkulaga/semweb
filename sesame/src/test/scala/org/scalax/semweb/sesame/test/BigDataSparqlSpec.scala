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
import org.scalax.semweb.rdf.{Trip, IRI}
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.Trip
import org.scalax.semweb.sparql.Pat

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
     val queryFreeze= db.justSelect(wrongQuery)
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
      val queryLove= db.justSelect(query.stringValue)
      queryLove.isSuccess shouldBe  true
      queryLove.get.toList.size shouldEqual 6

      db.shutDown() // shutting down
    }


    val del: Delete = DELETE (
      DATA (
        Trip(
          IRI("http://denigma.org/actors/resources/Daniel"),
          IRI("http://denigma.org/relations/resources/loves"),
          IRI("http://denigma.org/actors/resources/RDF")
        )
      )
    )

    val ins: Insert = INSERT (
      DATA (
        Trip(
          IRI("http://denigma.org/actors/resources/Anton"),
          IRI("http://denigma.org/relations/resources/hates"),
          IRI("http://denigma.org/actors/resources/Anton")
        )
      )
    )

    "delete and insert data right" in {
      val db = BigData(true) //cleaning the files and initializing the database

      self.addTestData(db) //add test data ( see SimpleTestData )

      db.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 6
      db.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1


      db.update(del.stringValue)

      db.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      db.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1

      db.update(ins.stringValue)
      db.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      db.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 2

      db.shutDown()

    }

    "insert blank nodes write" in {
      val db = BigData(true) //cleaning the files and initializing the database

      self.addTestData(db) //add test data ( see SimpleTestData )

      db.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 6
      db.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1


      db.update(del.stringValue)

      db.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      db.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1

      db.update(ins.stringValue)
      db.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      db.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 2

      db.shutDown()

    }


  }
  
  
  
}
