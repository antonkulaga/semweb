package org.scalax.semweb.sesame.test

import java.io.InputStream

import org.scalax.semweb.messages.Results.SelectResults
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex.Shape

import scala.util.Try

import java.io.InputStream

import org.scalatest.{Matchers, WordSpec}
import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.shex._
import org.scalax.semweb.rdf._
import org.scalax.semweb.sparql.{GRAPH, DATA, INSERT}
import org.openrdf.model.{Value, URI, Resource}
import org.scalax.semweb.shex._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.StringLiteral
import org.scalax.semweb.rdf.Quad
import org.scalax.semweb.rdf.Trip
import org.scalax.semweb.sesame._
import scala.util.Try
import org.scalax.semweb.sesame._

class ModelShapeLoaderSpec  extends  WordSpec with Matchers{

  def loadData(db:BigData) = {
    val st: InputStream = getClass.getResourceAsStream("/genes_shape.ttl")
    db.parseStream("gene_shapes.ttl",st)
    val dt: InputStream = getClass.getResourceAsStream("/genes_data.ttl")
    db.parseStream("genes_data.ttl",dt)
    val ont: InputStream = getClass.getResourceAsStream("/ontology.ttl")
    db.parseStream("ontology.ttl",ont)
    val web: InputStream = getClass.getResourceAsStream("/gero.longevityalliance.org.ttl")
    db.parseStream("gero.longevityalliance.org.ttl",web)


  }

  def loadQueryWithShape(db:BigData,query:String, shape:Shape): Try[Set[PropertyModel]] =
  {
    import org.scalax.semweb.sesame._
      db.selectQuery(query){ (q,con,qr)=>
        val res = qr.evaluate().toSelectResults
        val rows: List[Map[String, RDFValue]] = res.rows
        val resources = rows.map(r=>r.collectFirst{case (key,res:Res)=>res}.get:Resource).toSet
        val models = db.loadPropertyModelsByShape(shape,resources)//this.loadPropertyModelsByShape(shape,resources)
        models
      }.flatten
  }



  "Shapes" should {


    "load property models" in {

      val db = BigData(true)

      loadData(db)


      val gero = IRI("http://gero.longevityalliance.org/")
      val entrez = gero / "has_ENTREZID"

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
      //db.read{   con=>con.getStatements(null,null,null,true).map{    case st=>db.lg.error("STATEMENT = "+st.toString)      }

      val res = gero / "Evidence_Shape"

      val shop: Try[Shape] = db.loadShape(res)
      shop.isSuccess shouldEqual true
      val shape = shop.get
      shape.id.asResource shouldEqual res

      shape.arcSorted().size shouldEqual 13

      val query = "SELECT ?gene WHERE { ?gene <http://gero.longevityalliance.org/has_ENTREZID> ?entrez   }"
      db.read{con=>
        val sts =  con.getStatements(null,null,null,true).toSeq
          sts.foreach{     st=>
            db.lg.info(st.getObject.stringValue + " | "+     (Value2RDFValue(st.getObject):RDFValue).stringValue+"\n")
      }}

      val mods = loadQueryWithShape(db,query,shape)
      val models: Set[PropertyModel] = mods.get
      models.size shouldEqual 9
      val first = models.head
      first.properties.keys.size shouldEqual 13
      //db.lg.error("\nDATA OF MODELS = \n"+models.map(m=>m.properties).toSeq)
      val geno = models.find(m=>m.properties.contains(entrez) && m.properties(entrez).exists(p=>p.stringValue.contains("31220")))
      val gene = geno.get
      gene.properties.size shouldEqual 13
      gene.properties(entrez) shouldEqual IRI("http://www.ncbi.nlm.nih.gov/gene/?term=/31220")
      gene.properties(RDFS.SUBCLASSOF) shouldEqual gero / "growth_factors "





      db.shutDown()

    }
  }
}
