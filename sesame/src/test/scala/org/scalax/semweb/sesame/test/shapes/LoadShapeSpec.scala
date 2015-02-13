package org.scalax.semweb.sesame.test.shapes

import java.io.{StringReader, InputStream}

import org.scalatest.{Matchers, WordSpec}
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.sesame.test.classes.{BigData, GeneLoader}
import org.scalax.semweb.sesame.test.data.Genes
import org.scalax.semweb.shex._

import scala.util.Try

class LoadShapeSpec  extends  WordSpec with Matchers with GeneLoader {

  "Shapes" should {


    "be loaded by name" in {

      val db = BigData(true)
      val st: InputStream = getClass.getResourceAsStream("/shapes.ttl")
      db.parseStream("shapes.ttl",st)
      val res = IRI("http://gero.longevityalliance.org/Evidence_Shape")

      val shop: Try[Shape] = db.loadShape(res)
      shop.isSuccess shouldEqual true
      val shape = shop.get
      shape.id.asResource shouldEqual res
      shape.arcSorted().size shouldEqual 13

      db.shutDown()

    }

    "have optional parameters like title and priority" in {
      val db = BigData(true)
      loadData(db)
      val res = IRI("http://gero.longevityalliance.org/Evidence_Shape")
      val shapo: Try[Shape] = db.loadShape(res)
      shapo.isSuccess shouldEqual true
      val shape = shapo.get
      val arcs = shape.arcSorted()


      arcs.exists(arc=>arc.title.contains("DB Object ID")) shouldEqual true
      arcs.exists(arc=>arc.title.contains("Tissue")) shouldEqual true
      arcs.exists(arc=>arc.title.contains("Publication")) shouldEqual true
      arcs.exists(arc=>arc.title.contains("Influence")) shouldEqual true
      arcs.exists(arc=>arc.title.contains("Evidence Code")) shouldEqual true
      arcs.exists(arc=>arc.priority.contains(5)) shouldEqual true

      db.shutDown()
    }
    
    "write and read back from turtle" in {
     val sh = Genes.evidenceShape
     val res = sh.id.asResource
     val str = Genes.showEvidence() //write str to turtle
     val db = BigData(true)
     val reader = new StringReader(str)

     val p = db.parseReader(reader)
     p.isSuccess shouldEqual true
    val shop: Try[Shape] = db.loadShape(res)
    shop.isSuccess shouldEqual true
    val shape = shop.get
    shape.id.asResource shouldEqual res
    shape.arcSorted().size shouldEqual 13
    
     db.shutDown()
     
    }
  }
}
