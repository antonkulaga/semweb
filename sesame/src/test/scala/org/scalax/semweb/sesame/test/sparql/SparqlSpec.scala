package org.scalax.semweb.sesame.test.sparql

import org.openrdf.model.Statement
import org.openrdf.repository.RepositoryResult
import org.scalatest.{Matchers, _}
import org.scalax.semweb.rdf.{IRI, Trip}
import org.scalax.semweb.sesame._
import org.scalax.semweb.sesame.test.classes.{BigData, SimpleTestData}
import org.scalax.semweb.sparql.{Pat, _}

import scala.util.Try

/**
 * Tests SPARQL DSL on bigdata
 */
class SparqlSpec  extends  WordSpec with Matchers with SimpleTestData {
  self=> //alias to this, to avoid confusion

  "Bigdata spec" should {

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

      val query2 =  SELECT(?("s"), ?("o") ) WHERE (
        Pat(?("s"), IRI("http://denigma.org/relations/resources/loves"), ?("o")),
        FILTER(STR_STARTS(STR(?("o")),s"http://denigma.org/actors/resources/Immort"))
        )

      val queryLove2= db.justSelect(query2.stringValue)
      queryLove2.isSuccess shouldBe  true
      queryLove2.get.toList.size shouldEqual 5


      val query3 =  SELECT(?("s"), ?("o") ) WHERE (
        Pat(?("s"), IRI("http://denigma.org/relations/resources/loves"), ?("o")),
        FILTER(STR_STARTS(STR(?("o")),s"http://denigma.org/actors/resources/Immort"))
        )

      val queryLove3= db.justSelect(query2.stringValue)
      queryLove3.isSuccess shouldBe  true
      queryLove3.get.toList.size shouldEqual 5


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

    val cont1 = IRI("http://context/1/")
    val cont2 = IRI("http://context/2/")
    val cont3 = IRI("http://context/3/")

    "support named graphs" in {
      val db = BigData(true) //cleaning the files and initializing the database

      def selProp(prop:IRI,cont:IRI) = SELECT(?("o"),?("any")) FROM cont WHERE Pat( ?("o"),prop,?("any"))


      val i1 = INSERT (
        DATA.INTO (cont1)(
        Trip(Daniel,loves,RDF),
        Trip(Liz,loves,Immortality),
        Trip(Edouard, loves, Immortality)
        )
      )

      db.update(i1.stringValue)


      db.read{ con=>con.getStatements(null,loves,null,false,cont1).toList }.get.size shouldEqual 3
      db.read{ con=>con.getStatements(null,hates,null,false,cont1).toList }.get.size shouldEqual 0
      db.read{ con=>con.getStatements(null,loves,null,false,cont2).toList }.get.size shouldEqual 0
      db.read{ con=>con.getStatements(null,hates,null,false,cont2).toList }.get.size shouldEqual 0
      db.read{ con=>con.getStatements(null,loves,null,false,cont3).toList }.get.size shouldEqual 0
      db.read{ con=>con.getStatements(null,hates,null,false,cont3).toList }.get.size shouldEqual 0

      db.justSelect(selProp(loves,cont1).stringValue).get.size shouldEqual 3
      db.justSelect(selProp(hates,cont1).stringValue).get.size shouldEqual 0


      val i2 = INSERT (
        DATA.INTO (cont2)(
        Trip(Anton, hates, RDF),
        Trip(Anton,loves,Immortality)
      )
      )
      db.update(i2.stringValue)



      db.read{ con=>con.getStatements(null,loves,null,false,cont1).toList }.get.size shouldEqual 3
      db.read{ con=>con.getStatements(null,hates,null,false,cont1).toList }.get.size shouldEqual 0
      db.read{ con=>con.getStatements(null,loves,null,false,cont2).toList }.get.size shouldEqual 1
      db.read{ con=>con.getStatements(null,hates,null,false,cont2).toList }.get.size shouldEqual 1
      db.read{ con=>con.getStatements(null,loves,null,false,cont3).toList }.get.size shouldEqual 0
      db.read{ con=>con.getStatements(null,hates,null,false,cont3).toList }.get.size shouldEqual 0



      db.justSelect(selProp(loves,cont2).stringValue).get.size shouldEqual 1
      db.justSelect(selProp(hates,cont2).stringValue).get.size shouldEqual 1


      val i3 = INSERT (
        DATA.INTO (cont3)(
        Trip(Daniel,loves,Immortality),
        Trip(Ilia, loves, Immortality)
      ))
      db.update(i3.stringValue)

      db.read{ con=>con.getStatements(null,loves,null,false,cont1).toList }.get.size shouldEqual 3
      db.read{ con=>con.getStatements(null,hates,null,false,cont1).toList }.get.size shouldEqual 0
      db.read{ con=>con.getStatements(null,loves,null,false,cont2).toList }.get.size shouldEqual 1
      db.read{ con=>con.getStatements(null,hates,null,false,cont2).toList }.get.size shouldEqual 1
      db.read{ con=>con.getStatements(null,loves,null,false,cont3).toList }.get.size shouldEqual 2
      db.read{ con=>con.getStatements(null,hates,null,false,cont3).toList }.get.size shouldEqual 0

      db.justSelect(selProp(loves,cont3).stringValue).get.size shouldEqual 2
      db.justSelect(selProp(hates,cont3).stringValue).get.size shouldEqual 0


      db.read{ con=>con.getStatements(null,loves,null,false,cont1,cont2,cont3).toList }.get.size shouldEqual 6
      db.read{ con=>con.getStatements(null,hates,null,false,cont1,cont2,cont3).toList }.get.size shouldEqual 1

      db.shutDown()
    }
  }

  
  
}
