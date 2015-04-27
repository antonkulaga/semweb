package org.denigma.semweb.sesame.test.shapes

import java.io.InputStream

import org.denigma.semweb.sesame.test.classes.{GeneLoader, BigData}
import org.denigma.semweb.sesame.test.data.GeneSchemaData
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

class ModelShapeLoaderSpec  extends  WordSpec with Matchers with GeneLoader with GeneSchemaData
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

  "Shapes" should {



    "load property models" in {

      val db = BigData(true)

      loadData(db)

      val res = gero / "Evidence_Shape"

      val shop: Try[Shape] = db.loadShape(res)
      shop.isSuccess shouldEqual true
      val shape = shop.get
      shape.id.asResource shouldEqual res

      shape.arcSorted().size shouldEqual 12

      val query = "SELECT ?gene WHERE { ?gene <http://gero.longevityalliance.org/has_ENTREZID> ?entrezId   }"

      val mods = loadQueryWithShape(db,query,shape)
      val models: Set[PropertyModel] = mods.get
      models.size shouldEqual 9
      val first = models.head
      first.properties.keys.size shouldEqual 12
      val geno = models.find(m=>m.id.stringValue.contains("31220"))//m.properties.contains(entrezId) && m.properties(entrezId).exists(p=>p.stringValue.contains("31220")))
      val gene = geno.get
      gene.properties.size shouldEqual 12
      //gene.properties(entrezId) shouldEqual Set(IRI("http://ncbi.nlm.nih.gov/gene/31220"))
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
      val qClass = db.queryExtractor.arcQuery(cl)

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
      val codes: SelectQuery = db.queryExtractor.arcQuery(codo.get)
      //println("CODES\n"+codes.stringValue)
      val rCodes = db.justSelect(codes.stringValue)
      val cds = rCodes.get
      cds.size shouldEqual 10

      val dato = arcs.collectFirst{
        case arc @ ArcRule(  id: Label, name, valueClass,occurs: Cardinality,  actions,  priority,  Some("Date"),   default)  => arc
      }

      dato.isDefined shouldBe true
      val data = dato.get
      val dates = db.queryExtractor.arcQuery(data)
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

      val qsh = db.queryExtractor.validShapeQuery(shape)
      qsh.isDefined shouldEqual true
      val propo: Try[GraphQueryResult] = db.justConstruct(qsh.stringValue)
      propo.isSuccess shouldEqual(true)
      val props = propo.get
      val sts: List[Statement] = props.toList
      val e = sts.count{st=>st.getPredicate.stringValue().contains("has_code")}
      e shouldEqual 9//loads everything
      val subunit = sts.count(st=>st.getPredicate.stringValue().contains("has_name") && st.getObject.stringValue().contains("Glutamate-cysteine_ligase_catalytic_subunit"))
      subunit shouldEqual 1
      //printGraph(props)
      val ps: Seq[PropertyModel] = db.modelStatementsExtractor.extractFromStatements(sts)
      ps.exists(p=>p.properties.exists(p=>p._1.stringValue.contains("has_name"))) shouldBe true
      ps.exists(p=>p.properties.exists(p=>p._2.exists(s=>s.stringValue.contains("Glutamate-cysteine_ligase_catalytic_subunit")))) shouldBe true
      val totalProps = ps.foldLeft(0)((acc,p)=>acc+p.properties.size)
      totalProps shouldEqual (12*9-1)
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

      val qsh = db.queryExtractor.invalidShapeQuery(shape)
      val propo: Try[GraphQueryResult] = db.justConstruct(qsh.stringValue)
      propo.isSuccess shouldEqual true
      val props = propo.get
      val sts = props.toList
      val e = sts.count{st=>st.getPredicate.stringValue().contains("has_name")}
      e shouldEqual 3 //loads everything
      //printGraph(props)
      val ps = db.modelStatementsExtractor.extractFromStatements(sts)
      ps.size shouldEqual 3
      val h = ps.head
      h.properties.foreach{ case (p,v) =>v.size shouldEqual 1}
    }
    
    "load models " in {
      val db = BigData(true)

      loadData(db)

      val broken: InputStream = getClass.getResourceAsStream("/broken_genes.ttl")
      db.parseStream("broken_genes.ttl",broken)

      val res = gero / "Evidence_Shape"


      val shop: Try[Shape] = db.loadShape(res)
      shop.isSuccess shouldEqual true
      val shape = shop.get
      val tmodels = db.loadShapedModels(shape)
      tmodels.isSuccess shouldEqual true
      val models = tmodels.get
      val (valid,invalid) = models.partition(m=>m.validation==Valid)
      valid.size shouldEqual 9
      invalid.size shouldEqual 3
    }

    def printGraph(g:GraphQueryResult) = {
      val t = g.toList
      println("CONSTRUCT RESULTS = \n"+t.mkString("\n"))
    }

  }
}