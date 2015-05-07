package org.denigma.semweb.sesame.test.sparql

import com.bigdata.rdf.model.{BigdataBNodeImpl, BigdataStatement}
import org.denigma.semweb.sesame.test.classes.{BigData, SimpleTestData}
import org.openrdf.model.Value
import org.scalatest.{Matchers, _}
import org.denigma.semweb.rdf.IRI
import org.denigma.semweb.sesame._
import org.denigma.semweb.sesame.test.classes.SimpleTestData
import org.denigma.semweb.sparql.{Pat, _}

/**
 * Tests SPARQL DSL on bigdata
 */
class ReificationSpec  extends  WordSpec with Matchers with SimpleTestData {
  self=> //alias to this, to avoid confusion

  "Reification" should {

    val tells = this.predicate("tells_that")

    def tellThat(sub: IRI,st:BigdataStatement)(implicit con:BigData#WriteConnection){
      val node = con.getValueFactory.createBNode(st)
      val rst: BigdataStatement = con.getValueFactory.createStatement(sub,tells,node)
      con.add(rst)
      rst
    }

    "allow creating ids for statements and getting them back in queries " in {
      import org.denigma.semweb.rdf

      val db = BigData(true) //cleaning the files and initializing the database
      self.addTestData(db) //add test data ( see SimpleTestData )
      db.write{ con=>
        val dsts = con.getStatements(Daniel,null,null,true).toList
        dsts.collect{  case s:BigdataStatement=>   tellThat(Liz,s)(con)  }
        val asts = con.getStatements(Anton,null,null,true).toList
        asts.collect{  case s:BigdataStatement=>   tellThat(Daniel,s)(con)  }
      }

      val sid = ?("id")

      val query = SELECT(sid) WHERE Pat(Liz:IRI,tells:IRI,sid)

      val stories = db.justSelect(query.stringValue).get.extractVarValues(sid).toList

      stories.forall{
        case s:BigdataBNodeImpl if s.isStatementIdentifier=>  s.getStatement.getSubject==Daniel
        case other=>false
      } shouldEqual true

      val who = ?("who")
      val informer = ?("informer")
      val statement = ?("statement")
      val rdrQuery = SELECT(who,informer) WHERE (
        Pat( informer,tells:IRI,statement),
        BIND(RDR(who,hates:IRI,RDF:IRI),statement)
        )
      //println("RDR QUERY = \n"+rdrQuery.stringValue)

      val tales = db.justSelect(rdrQuery.stringValue).get.extractVars(who,informer)
      tales.size shouldEqual 2
      tales.map(_._2).toSet shouldEqual Set(Anton,Daniel)

      db.shutDown() // shutting down
    }

    "add the same message several times for each context" in {
      val con1 = IRI("http://context1")
      val con2 = IRI("http://context2")

      val db = BigData(true)
      db.write{con=>
        con.add(Daniel,loves,RDF,con1,con2) //adds it 2 times
        con.add(Anton,hates,RDF,con1) //adds it 1 time
      }
      val sts = db.read{
        con=> con.getStatements(null,null,null,true,con1,con2).toList
      }
      sts.get.size shouldEqual 3
      db.shutDown()
    }



  }


}

