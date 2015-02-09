package org.scalax.semweb.sesame.test.shapes


import java.io.InputStream

import org.openrdf.model.{Statement, Resource}
import org.openrdf.query.GraphQueryResult
import org.scalatest.{Matchers, WordSpec}
import org.scalax.semweb.rdf.{IRI, _}
import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.sesame.test.classes.{GeneSchemaData, BigData, GeneLoader}
import org.scalax.semweb.shex.{Shape, _}
import org.scalax.semweb.sesame._
import org.scalax.semweb.sparql.SelectQuery
import org.scalax.semweb.sesame._

import scala.util.Try

class ModelShapeLoaderSpec  extends  WordSpec with Matchers with GeneLoader with GeneSchemaData
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
      val geno = models.find(m=>m.properties.contains(entrezId) && m.properties(entrezId).exists(p=>p.stringValue.contains("31220")))
      val gene = geno.get
      gene.properties.size shouldEqual 13
      gene.properties(entrezId) shouldEqual Set(IRI("http://ncbi.nlm.nih.gov/gene/31220"))
      gene.properties(RDFS.SUBCLASSOF) shouldEqual Set(gero / "growth_factors")

      db.shutDown()

    }

    "load valid property models" in {

      val db = BigData(true)

      loadData(db)

      val broken: InputStream = getClass.getResourceAsStream("/broken_genes.ttl")
      db.parseStream("broken_genes.ttl",broken)

      val res = gero / "Evidence_Shape"

      val shop: Try[Shape] = db.loadShape(res)
      shop.isSuccess shouldEqual true
      val shape = shop.get
      val arcs = shape.arcSorted()
      val classo = arcs.collectFirst{
        case arc @ ArcRule(  id: Label, name, ValueStem(st),occurs: Cardinality,  actions,  priority,  Some("Class"),   default) =>
          arc
      }
      classo.isDefined shouldBe true
      val cl = classo.get
      val qClass = db.extractor.queryExtractor.arcQuery(cl)

      //println("\n"+qClass.stringValue)

      //LOAD FROM ARC WITH CLASSES

      val r = db.justSelect(qClass.stringValue)
      r.isSuccess shouldEqual true
      r.get.size should be > 15

      //println("ARCS = \n"+arcs.toString)

      //LOAF FROM ARC WITH ALLOWED VALUES

      val codo: Option[ArcRule] = arcs.collectFirst{
        case arc @ ArcRule(  id: Label, name, valueClass,occurs: Cardinality,  actions,  priority,  Some("Evidence Code"),   default)  => arc
      }
      //println(codo.toString)
      codo.isDefined shouldBe true
      val codes: SelectQuery = db.extractor.queryExtractor.arcQuery(codo.get)
      //println("CODES\n"+codes.stringValue)
      val rCodes = db.justSelect(codes.stringValue)
      val cds = rCodes.get
      cds.size shouldEqual 10

      val dato = arcs.collectFirst{
        case arc @ ArcRule(  id: Label, name, valueClass,occurs: Cardinality,  actions,  priority,  Some("Date"),   default)  => arc
      }

      dato.isDefined shouldBe true
      val data = dato.get
      val dates = db.extractor.queryExtractor.arcQuery(data)
      val ds = db.justSelect(dates.stringValue)
      ds.isSuccess shouldEqual true
      ds.get.size shouldEqual 10
      //db.sele

      //println(r.get.toListMap.mkString("\n"))

      db.shutDown()
    }

    "load models for valid shapes" in {

      val db = BigData(true)

      loadData(db)

      val broken: InputStream = getClass.getResourceAsStream("/broken_genes.ttl")
      db.parseStream("broken_genes.ttl",broken)

      val res = gero / "Evidence_Shape"


      val shop: Try[Shape] = db.loadShape(res)
      shop.isSuccess shouldEqual true
      val shape = shop.get


      val arcs = shape.arcSorted()
      val classo = arcs.collectFirst{
        case arc @ ArcRule(  id: Label, name, ValueStem(st),occurs: Cardinality,  actions,  priority,  Some("Class"),   default) =>
          arc
      }
      val classShape = db.extractor.arc2Shape(classo.get)

      val sh = db.extractor.queryExtractor.validShapeQuery(shape)
      sh.isDefined shouldEqual(true)
      val qsh = sh.get
      val propo: Try[GraphQueryResult] = db.justConstruct(qsh.stringValue)
      propo.isSuccess shouldEqual(true)
      val props = propo.get
      val sts = props.toList
      val e = sts.count{st=>st.getPredicate.stringValue().contains("has_ENTREZID")}
      e shouldEqual 9//loads everything
      //printGraph(props)

      db.shutDown()
    }



    "load models for invalid shapes" in {

      val db = BigData(true)

      loadData(db)

      val broken: InputStream = getClass.getResourceAsStream("/broken_genes.ttl")
      db.parseStream("broken_genes.ttl",broken)

      val res = gero / "Evidence_Shape"


      val shop: Try[Shape] = db.loadShape(res)
      shop.isSuccess shouldEqual true
      val shape = shop.get


      val arcs = shape.arcSorted()
      val classo = arcs.collectFirst{
        case arc @ ArcRule(  id: Label, name, ValueStem(st),occurs: Cardinality,  actions,  priority,  Some("Class"),   default) =>
          arc
      }
      val classShape = db.extractor.arc2Shape(classo.get)

      val sh = db.extractor.queryExtractor.invalidShapeQuery(shape)
      sh.isDefined shouldEqual(true)
      val qsh = sh.get
      val propo: Try[GraphQueryResult] = db.justConstruct(qsh.stringValue)
      propo.isSuccess shouldEqual(true)
      val props = propo.get
      val sts = props.toList
      val e = sts.count{st=>st.getPredicate.stringValue().contains("has_ENTREZID")}
      e shouldEqual 3 //loads everything
      //printGraph(props)

      db.shutDown()
    }

/*
    "construct graphs" in {
      val db = BigData(true)
      loadData(db)

      val broken: InputStream = getClass.getResourceAsStream("/broken_genes.ttl")
      db.parseStream("broken_genes.ttl",broken)

      val con =
        """
          |PREFIX ex: <http://example.org>
          |CONSTRUCT {
          |?s ?p ?o
          |?st ex:was_told_by ex:Bob
          |} WHERE
          |{
          |?s ?p ?o
          |{
          | SELECT ?st WHERE {
          | ?s ?p ?o
          | BIND (<<?s ?p ?o>> AS ?st) }
          |}
          |}
        """.stripMargin
      println("CONSTRUCT QEUALS = \n"+con)
      val g = db.justConstruct(con)
      g.isSuccess shouldEqual true
      printGraph(g.get)
      db.shutDown()

    }
*/



    def printGraph(g:GraphQueryResult) = {
      val t = g.toList
      println("CONSTRUCT RESULTS = \n"+t.mkString("\n"))
    }

  }
}