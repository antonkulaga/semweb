package org.denigma.semweb.sesame.test.shapes

import java.io.InputStream

import org.denigma.schemas.genes.GeneSchemaData
import org.denigma.schemas.web.ChatSchema
import org.denigma.semweb.sesame.test.classes.BigData
import org.denigma.semweb.sesame.test.data.{BlogTestData, GeneLoader}
import org.openrdf.model.{Statement, Resource}
import org.openrdf.query.{QueryLanguage, GraphQueryResult}
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
class ComplexShapesSpec  extends WordSpec with Matchers  with BlogTestData
{
  self=>


/*  def loadQueryWithShape(db:BigData,query:String, shape:Shape): Try[Set[PropertyModel]] =
  {
    import org.denigma.semweb.sesame._
    db.selectQuery(query){ (q,con,qr)=>
      val res = qr.evaluate().toSelectResults
      val rows: List[Map[String, RDFValue]] = res.rows
      val resources = rows.map(r=>r.collectFirst{case (k,r:Res)=>r}.get:Resource).toSet
      val models = db.loadPropertyModelsByShape(shape,resources)//this.loadPropertyModelsByShape(shape,resources)
      models
    }.flatten
  }*/

  "Complex shape features" should {


    "allow to load complex shapes" in {

      val db = BigData(true)
      this.addTestData(db)

      val shapeRes = this.messageShapeRes

      val tryMess: Try[Shape] = db.loadShape(shapeRes)
      tryMess.isSuccess shouldEqual true
      val mshape = tryMess.get
      val rules = mshape.arcSorted
      rules.size shouldEqual 7
      val refs: Set[Res] = mshape.shapeRefs
      refs.contains(userShortShape.id.asResource) shouldEqual true
      refs.contains(messageShapeRes) shouldEqual true


      val trySh = db.loadShEx(shapeRes,self.prefixes)
      trySh.isSuccess shouldEqual(true)
      val sh = trySh.get
      sh.id.asResource shouldEqual(shapeRes)
      sh.rules.size shouldEqual 2
      sh.rules.headOption.get shouldEqual mshape
/*

      val q = db.queryExtractor.validShexQuery(sh)
      println(q.stringValue)

      val models = loadShExModels(db,sh)
*/

      //db.queryExtractor.validShexQuery(shexp = ShEx())
      db.shutDown()

    }
  }

}
