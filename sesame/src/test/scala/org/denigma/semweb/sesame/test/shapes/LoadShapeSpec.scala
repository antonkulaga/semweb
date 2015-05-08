package org.denigma.semweb.sesame.test.shapes

import java.io.{InputStream, StringReader}

import org.denigma.semweb.rdf.IRI
import org.denigma.semweb.sesame.test.classes.BigData
import org.denigma.semweb.sesame.test.data.{Genes, GeneLoader}
import org.denigma.semweb.shex._
import org.scalatest.{Matchers, WordSpec}

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
      shape.arcSorted.size shouldEqual 12//13

      db.shutDown()

    }

    "have optional parameters like title and priority" in {
      val db = BigData(true)
      loadData(db)
      val res = IRI("http://gero.longevityalliance.org/Evidence_Shape")
      val shapo: Try[Shape] = db.loadShape(res)
      shapo.isSuccess shouldEqual true
      val shape = shapo.get
      val arcs = shape.arcSorted


      arcs.exists(arc=>arc.title.contains("DB Object ID")) shouldEqual true
      arcs.exists(arc=>arc.title.contains("Tissue")) shouldEqual true
      arcs.exists(arc=>arc.title.contains("Publication")) shouldEqual true
      arcs.exists(arc=>arc.title.contains("Influence")) shouldEqual true
      arcs.exists(arc=>arc.title.contains("Evidence Code")) shouldEqual true
      arcs.exists(arc=>arc.priority.contains(5)) shouldEqual true

      db.shutDown()
    }
    
    "write and read back from turtle" in {
      val ruleNum = 12 //13
      val gero = IRI("http://gero.longevityalliance.org/")
      val entrez = IRI("http://ncbi.nlm.nih.gov/gene/")
      val sh = Genes.evidenceShape
      val res = sh.id.asResource
      val db = BigData(true)
      val str = Genes.showEvidence() //write str to turtle
      //this.writeToFile("/home/antonkulaga/Documents/evidence.ttl",str)
      val reader = new StringReader(str)
      val p = db.parseReader(reader)
      p.isSuccess shouldEqual true
      val shop: Try[Shape] = db.loadShape(res)
      shop.isSuccess shouldEqual true
      val shape = shop.get
      shape.id.asResource shouldEqual res
      shape.arcSorted.size shouldEqual ruleNum
      val frules=db.read{
        con=>
          db.extractor.fieldRulesExtractor.extractFieldRules(Genes.evidenceShape.id.asResource,con)
      }
      assert(frules.isSuccess)
      val soa = shape.subjectRuleOption
      assert(soa.isDefined)
      assert(soa.get == Genes.subjectDefs)
      db.shutDown()
     
    }
  }
}
