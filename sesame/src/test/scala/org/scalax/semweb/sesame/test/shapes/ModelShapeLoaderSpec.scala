package org.scalax.semweb.sesame.test.shapes

import org.openrdf.model.Resource
import org.scalatest.{Matchers, WordSpec}
import org.scalax.semweb.rdf.{IRI, _}
import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.sesame.test.classes.{BigData, GeneLoader}
import org.scalax.semweb.shex.{Shape, _}

import scala.util.Try

class ModelShapeLoaderSpec  extends  WordSpec with Matchers with GeneLoader
{

  def loadQueryWithShape(db:BigData,query:String, shape:Shape): Try[Set[PropertyModel]] =
  {
    import org.scalax.semweb.sesame._
      db.selectQuery(query){ (q,con,qr)=>
        val res = qr.evaluate().toSelectResults
        val rows: List[Map[String, RDFValue]] = res.rows
        val resources = rows.map(r=>r.collectFirst{case (k,r:Res)=>r}.get:Resource).toSet
        val models = db.loadPropertyModelsByShape(shape,resources)//this.loadPropertyModelsByShape(shape,resources)
        models
      }.flatten
  }

  "Shapes" should {

    val gero = IRI("http://gero.longevityalliance.org/")

    lazy val (entrezId:IRI,adb:IRI,objId:IRI,symbol:IRI, qualifier:IRI, go:IRI,
    ref:IRI, code:IRI , from:IRI, aspect:IRI,  dbObjectName:IRI, synonym:IRI, tp:IRI,
    taxon:IRI, date:IRI, assigned:IRI, extension:IRI, product:IRI,
    clazz:IRI, tissue:IRI, influence:IRI
      ) =
      (gero / "has_ENTREZID",gero / "from_db" ,  gero / "is_DB_object" ,  gero / "has_symbol", gero / "has_qualifier", gero / "has_GO",
        gero / "has_ref", gero / "has_code" , gero / "is_from",
        gero / "has_aspect", gero / "has_name", gero / "has_synonym", gero / "of_type",
        gero / "from_model_organism", gero / "added_on", gero / "is_assigned_by",
        gero / "has_extension", gero / "is_product",
        RDFS.SUBCLASSOF, gero / "from_tissue", gero / "has_influence"
        )


    "load property models" in {

      val db = BigData(true)

      loadData(db)

      val res = gero / "Evidence_Shape"

      val shop: Try[Shape] = db.loadShape(res)
      shop.isSuccess shouldEqual true
      val shape = shop.get
      shape.id.asResource shouldEqual res

      shape.arcSorted().size shouldEqual 13

      val query = "SELECT ?gene WHERE { ?gene <http://gero.longevityalliance.org/has_ENTREZID> ?entrezId   }"

      val mods = loadQueryWithShape(db,query,shape)
      val models: Set[PropertyModel] = mods.get
      models.size shouldEqual 9
      val first = models.head
      first.properties.keys.size shouldEqual 13
      /*db.lg.error("RULES = "+shape.arcSorted().map(s=>s.toString+"\n"))
      db.lg.error("\nDATA OF MODELS = \n"+models.map(m=>m.properties).toSeq)*/
      val geno = models.find(m=>m.properties.contains(entrezId) && m.properties(entrezId).exists(p=>p.stringValue.contains("31220")))
      val gene = geno.get
      gene.properties.size shouldEqual 13
      gene.properties(entrezId) shouldEqual Set(IRI("http://ncbi.nlm.nih.gov/gene/31220"))
      gene.properties(RDFS.SUBCLASSOF) shouldEqual Set(gero / "growth_factors")

      db.shutDown()

    }

    "load property models with contexts" in {

    }
  }
}
