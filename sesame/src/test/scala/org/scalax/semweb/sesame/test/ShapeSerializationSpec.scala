package org.scalax.semweb.sesame.test

import org.scalatest.{Matchers, WordSpec}
import org.openrdf.model._
import org.scalax.semweb.rdf._
import org.scalax.semweb.shex._
import org.scalax.semweb.sparql._
import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.sesame._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.BlankNode
import org.scalax.semweb.rdf.Trip
import org.scalax.semweb.rdf.Quad
import org.openrdf.model.impl.{URIImpl, BNodeImpl}
import com.bigdata.rdf.model.BigdataBNodeImpl
import org.scalax.semweb.rdf.vocabulary.USERS.props

/**
 *
 */
class ShapeSerializationSpec extends  WordSpec with Matchers {
  self=> //alias to this, to avoid confusion

  "RDF shapes" should {

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
    val quads: Set[Quad] = sh.asQuads(c)


    val ins = INSERT (
      DATA (
        GRAPH(c,quads.map(q=>Trip(q.sub,q.pred,q.obj)).toList)
      )
    )


    "be saved to bigdata" in {

      val db = BigData(true) //cleaning the files and initializing the database


      val u = db.update(ins.stringValue)
      u.isSuccess shouldEqual true

      val con = db.readConnection

      con.hasStatement(page,RDF.TYPE,rs / "ResourceShape",true,c) shouldEqual true
      con.hasStatement(page,ArcRule.property,null,true,c) shouldEqual true

      con.hasStatement(null, NameTerm.property,   WI.pl("title"), true, c) shouldEqual true

      con.hasStatement(null, rs / "valueType" ,   XSD.StringDatatypeIRI, true, c) shouldEqual true
      con.hasStatement(null, rs / "occurs" ,   rs / "Exactly-one" , true, c) shouldEqual true

      con.hasStatement(null, rs / "valueType" ,   FOAF.PERSON, true, c) shouldEqual true
      con.hasStatement(null, rs / "occurs" ,   rs / "One-or-many" , true, c) shouldEqual true

      val props = con.getStatements(page,ArcRule.property,null,false,c).filter{case st => st.getObject.isInstanceOf[Resource]}
      props.size shouldEqual(3)

      con.close()


      //UNCOMMENT FOLLOWING LINES TO SEE TIMEOUTS
      //val queryFreeze= db.select(wrongQuery)
      db.shutDown() // shutting down

    }

    "Load properties form saved shape" in {

      val db = BigData(true) //cleaning the files and initializing the database
      val u = db.update(ins.stringValue)
      u.isSuccess shouldEqual true
      val ids = db.loadAllShapeIds
      ids.isSuccess shouldEqual true
      ids.get == page

      object test extends ShapeBuilder(page)



      val con = db.readConnection

      con.hasStatement(page,RDF.TYPE,rs / "ResourceShape",true,c) shouldEqual true
      con.hasStatement(null, rs / "propDefinition" ,   WI.pl("title"), true, c) shouldEqual true
      con.hasStatement(null, rs / "valueType" ,   XSD.StringDatatypeIRI, true, c) shouldEqual true
      con.hasStatement(null, rs / "occurs" ,   rs / "Exactly-one" , true, c) shouldEqual true

      con.hasStatement(null, rs / "valueType" ,   FOAF.PERSON, true, c) shouldEqual true
      con.hasStatement(null, rs / "occurs" ,   rs / "One-or-many" , true, c) shouldEqual true


      val subs = con.subjects(NameTerm.property ,null,List(c))
      println("subjects = "+subs.toString())

      val tlt: Resource = con.subjects(NameTerm.property , title,List(c)).head


      con.hasSubjectFor(NameTerm.property,title,c)

      con.hasStatement(tlt,NameTerm.property,null,true,c) shouldEqual true

      //      con.hasObjectFor(tlt,NameTerm.property,c) shouldEqual true
//
//
//
//      val txt = con.subjects(NameTerm.property , text,c).head
//      val au = con.subjects(NameTerm.property , author,c).head
//
//
      val props = con resources(page,ArcRule.property,List(c:Resource))
//      props.size shouldEqual(3)
//      println("props = " + props.toString())
//
//      val un = (props++subs).toSet
//      un.size shouldEqual 3
//
//      con.hasSubjectFor(NameTerm.property,title,c) shouldEqual true
//      con.hasSubjectFor(NameTerm.property,text,c) shouldEqual true
//      con.hasSubjectFor(NameTerm.property,author,c) shouldEqual true
//
//      val r = props.find(_==tlt)
//      r.isDefined shouldEqual true
//
//      props.contains(tlt) shouldEqual true
//      props.contains(txt) shouldEqual true
//      props.contains(au) shouldEqual true




//      con.hasObjectFor(props(0),null,c) shouldEqual true
//      con.hasObjectFor(props(0),NameTerm.property,c) shouldEqual true
//      con.hasObjectFor(props(1),null) shouldEqual true
//      con.hasObjectFor(props(1),NameTerm.property,c) shouldEqual true
//      con.hasObjectFor(props(2),null,c) shouldEqual true
//      con.hasObjectFor(props(2),NameTerm.property,c) shouldEqual true

     val terms = props.flatMap{case p=>con objects(p,NameTerm.property:URI,List(c:Resource))}
     terms.size shouldEqual(3)

      val arcs = props.flatMap(p=>db.getArc(p,con)(List(c:Resource)))
      arcs.size shouldEqual 3
      arcs.foreach{a=>print(a.toString)}


      arcs.count{a=>a.name match {
          case NameTerm( tlt ) if tlt == title=>true
          case _ =>false
        }
      } shouldEqual 1

      arcs.count{a=>a.name match {
        case NameTerm( t ) if t == text =>true
        case _ =>false
      }
      } shouldEqual 1

      arcs.count{a=>a.name match {
        case NameTerm( au ) if au == author=>true
        case _ =>false
      }
      } shouldEqual 1

      val occs =  arcs.map(_.occurs)


      val shape = db.getShape(page,con)(List(c:Resource))

      shape.rule.isInstanceOf[AndRule] shouldEqual true
      val and = shape.rule.asInstanceOf[AndRule]
      val rules = and.conjoints.collect{case rule:ArcRule=>rule}
      rules.size shouldEqual 3

      con.close()

//      val arc = db.getArc(author.get.me,test,con)
//      arc.isDefined shouldEqual(true)
//
//
//      val she = db.loadShape(page)
//      she.isSuccess shouldEqual(true)
//      val sh = she.get
//      sh.rule match {
//        case and:AndRule =>
//          val rules = and.conjoints
//          rules.size shouldEqual(1)
//        case other=> this.fail("unlnown rule")
//      }


      db.shutDown()

    }
  }



}
