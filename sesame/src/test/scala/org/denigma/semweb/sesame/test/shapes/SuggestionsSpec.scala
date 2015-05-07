package org.denigma.semweb.sesame.test.shapes

import java.io.InputStream

import org.denigma.semweb.sesame.test.classes.{GeneLoader, BigData}
import org.denigma.semweb.sesame.test.data.genes.GeneSchemaData
import org.openrdf.model.{Statement, Resource}
import org.openrdf.query.GraphQueryResult
import org.scalatest.{Matchers, WordSpec}
import org.denigma.semweb.rdf.{IRI, _}
import org.denigma.semweb.rdf.vocabulary._
import org.denigma.semweb.sesame.test.classes.BigData
import org.denigma.semweb.shex.validation.Valid
import org.denigma.semweb.shex.{Shape, _}
import org.denigma.semweb.sesame._
import org.denigma.semweb.sparql.{CONSTRUCT, SelectQuery}
import org.denigma.semweb.sesame._

import scala.util.Try

class SuggestionsSpec  extends  WordSpec with Matchers with GeneLoader with GeneSchemaData
{


  def loadQueryWithShape(db:BigData,query:String, shape:Shape): Try[Set[PropertyModel]] =
  {
    import org.denigma.semweb.sesame._
    db.selectQuery(query){ (q,con,qr)=>
      val res = qr.evaluate().toSelectResults
      val rows: List[Map[String, RDFValue]] = res.rows
      val resources = rows.map(r=>r.collectFirst{case (k,r:Res)=>r}.get:Resource).toSet
      val models = db.loadPropertyModelsByShape(shape,resources)//this.loadPropertyModelsByShape(shape,resources)
      models
    }.flatten
  }

  "Shape suggestions" should {

    "load valid property models" in {

      val db = BigData(true)

      loadData(db)

      val res = gero / "Evidence_Shape"

      val name =  gero / "has_name" //9
      val arc = ArcRule.apply(name,RDF.VALUE,name / "rule")
      import org.denigma.semweb.sesame._
      import org.denigma.semweb.sparql._

      val sub = ?("s")
      val o = ?("o")
      val tp = "liga"

      val q = SELECT (o)  WHERE (db.queryExtractor.suggestPatterns(tp,arc,sub,o):_*)
      //println(q.stringValue)
      val result = db.justSelect(q.stringValue)
      result.isSuccess shouldEqual true
      result.get.size shouldEqual 2
      db.shutDown()
    }



    def printGraph(g:GraphQueryResult) = {
      val t = g.toList
      println("CONSTRUCT RESULTS = \n"+t.mkString("\n"))
    }

  }
}