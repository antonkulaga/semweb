package org.scalax.semweb.sesame.test

import java.io.InputStream

import org.scalatest.{Matchers, WordSpec}
import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.sesame.test.classes.BigData
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


class LoadByShapeSpec extends  WordSpec with Matchers {
  self=> //alias to this, to avoid confusion

  "Shapes" should {

    val page = WI.re("Page")

    object shape extends ShapeBuilder(page)

    val title = WI.pl("title")
    shape has title of XSD.StringDatatypeIRI occurs ExactlyOne result
    val text = WI.pl("text")
    shape has text of XSD.StringDatatypeIRI occurs ExactlyOne result
    val author = WI.pl("author")
    shape has author of FOAF.PERSON occurs Plus result

    //val pub = shape has WI.pl("published") of XSD.Date  occurs ExactlyOne result

    val sh = shape.result
    val c: IRI = WI.re("context")
    val shapeQuads: Set[Quad] = sh.asQuads(c)


    val insertShape = INSERT (
      DATA (
        GRAPH(c,shapeQuads.map(q=>Trip(q.sub,q.pred,q.obj)).toList)
      )
    )


    val paper = WI.re("paper")
    val pTitle = StringLiteral("It is a paper")
    val pText = StringLiteral("It is paper's text")
    val pAuthor = WI.re("Anton_Kulaga")

    val paperType = WI.re("Paper")

    val pm = Quads -- paper
    pm --RDF.TYPE--paperType--c
    pm -- title -- pTitle--c
    pm -- text -- pText--c
    pm -- author -- pAuthor--c
    pm -- WI.re("extras")--StringLiteral("extra information")--c

    val paperQuads = pm.quads

    val insertPaper = INSERT (
      DATA (
        GRAPH(c,paperQuads.map(q=>Trip(q.sub,q.pred,q.obj)).toList)
      )
    )

    val draft = WI.re("draft")

    val dAuthor = WI.re("Daniel_Wuttke")
    val dTitle = StringLiteral("Draft title")
    val extras = WI.re("extras")


    val dm = Quads --draft
    dm --RDF.TYPE--paperType--c
    dm -- title -- dTitle--c
    dm -- author -- dAuthor--c
    dm -- extras--StringLiteral("extra information 2")--c
    val draftQuads = dm.quads

    val insertDraft = INSERT (
      DATA (
        GRAPH(c,draftQuads.map(q=>Trip(q.sub,q.pred,q.obj)).toList)
      )
    )


    val ptm = Quads -- paperType
    ptm--WI.PLATFORM.HAS_SHAPE--page--c
    val insertOther = INSERT (
      DATA (
        GRAPH(c,ptm.quads.map(q=>Trip(q.sub,q.pred,q.obj)).toList++List(
        Trip(pAuthor,RDF.TYPE,FOAF.PERSON),  Trip(dAuthor,RDF.TYPE,FOAF.PERSON)
        ))
      )
    )


    "be used to load properties for resources" in {


      val db = BigData(true) //cleaning the files and initializing the database

      val u = db.update(insertShape.stringValue)
      u.isSuccess shouldEqual true

      val ip = insertPaper.stringValue

      val up = db.update(ip)
      up.isSuccess shouldEqual true

      val ud = db.update(insertDraft.stringValue)
      ud.isSuccess shouldEqual true

      val uo = db.update(insertOther.stringValue)
      uo.isSuccess shouldEqual true

      val con = db.readConnection
      con.getStatements(paper,null,null,true,c).size shouldEqual 5
      con.getStatements(draft,null,null,true,c).size shouldEqual 4
      con.close()

      val sho = db.loadShape(page)
      sho.isSuccess shouldEqual true
      val sh = sho.get
      val arcs: Set[ArcRule] = sh.rule.asInstanceOf[AndRule].conjoints.collect{case r:ArcRule=>r}
      arcs.size shouldEqual 3

      val shapes = db.loadShapesForType(paperType).get
      shapes.size shouldEqual 1
      val sl = shapes.head
      val rls: Set[ArcRule] = sl.rule.asInstanceOf[AndRule].conjoints.collect{case r:ArcRule=>r}
      rls.size shouldEqual 3

      val rco: Option[ArcRule] = rls.collectFirst{case rule if rule.name.isInstanceOf[NameTerm] && rule.name.asInstanceOf[NameTerm].property == author =>rule}
      rco.isDefined shouldEqual true
      val aur = rco.get

      val rco2: Option[ArcRule] = rls.collectFirst{case rule if rule.name.isInstanceOf[NameTerm] && rule.name.asInstanceOf[NameTerm].property == title =>rule}
      rco2.isDefined shouldEqual true
      val pt = rco2.get


      val con2 = db.readConnection

      val pau: (IRI, Seq[Value]) = db.extractor.propertyByArc(paper,author,aur)(con2)
      pau._2.size shouldEqual 1
      pau._2.head.stringValue() shouldEqual pAuthor.stringValue


      val pat: (IRI, Seq[Value]) = db.extractor.propertyByArc(paper,title,pt)(con2)
      pat._2.size shouldEqual 1
      pTitle.stringValue.contains(pat._2.head.stringValue()) shouldEqual true



      val dau: (IRI, Seq[Value]) = db.extractor.propertyByArc(draft,author,aur)(con2)
      dau._2.size shouldEqual 1
      dau._2.head.stringValue() shouldEqual dAuthor.stringValue

      val dat: (IRI, Seq[Value]) = db.extractor.propertyByArc(draft,title,pt)(con2)
      dat._2.size shouldEqual 1
      //dat._2.head.stringValue() shouldEqual dTitle

      con2.close()



      val ops: Try[Set[PropertyModel]] = db.loadPropertyModelsByShape(sl,Set(paper))
      ops.isSuccess shouldBe true
      val opl = ops.get
      opl.size shouldBe 1
      val op = opl.head
      op.isValid shouldEqual true
      op.properties(author).head shouldEqual pAuthor
      pTitle shouldEqual op.properties(title).head
      pText shouldEqual op.properties(text).head

      val ods: Try[PropertyModel] = db.loadModelByShape(sl,draft)
      ods.isSuccess shouldBe true
      val od = ods.get

      od.isValid shouldEqual false
      od.properties(author).head shouldEqual dAuthor
      dTitle shouldEqual od.properties(title).head
      od.properties(text).isEmpty shouldEqual true
      od.properties.get(extras).isEmpty shouldEqual true

      val vio = od.violations
      vio.size shouldEqual 1
      vio.head.value shouldEqual text

      db.shutDown()


    }

  }





}
