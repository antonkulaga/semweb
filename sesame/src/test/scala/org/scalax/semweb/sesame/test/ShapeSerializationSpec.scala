package org.scalax.semweb.sesame.test

import org.scalatest.{Matchers, WordSpec}
import scala.util.Try
import org.openrdf.repository.RepositoryResult
import org.openrdf.model.Statement
import org.scalax.semweb._
import org.scalax.semweb.rdf._
import org.scalax.semweb.shex._
import org.scalax.semweb.sparql._
import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.sesame._
import java.io.{File, PrintWriter}
import org.openrdf.rio.turtle.TurtleWriter

/**
 *
 */
class ShapeSerializationSpec extends  WordSpec with Matchers {
  self=> //alias to this, to avoid confusion

  "RDF shapes" should {

    val page = WI.re("Page")

    object shape extends ShapeBuilder(page)

    val title= shape has WI.pl("title") of XSD.StringDatatypeIRI occurs ExactlyOne result
    val text = shape has WI.pl("text") of XSD.StringDatatypeIRI occurs ExactlyOne result
    val author = shape has WI.pl("author") of FOAF.PERSON occurs Plus result
    //val pub = shape has WI.pl("published") of XSD.Date  occurs ExactlyOne result

    val sh = shape.result
    val c = WI.re("context")
    val quads: Set[Quad] = sh.asQuads(c)


    val ins = INSERT (
      DATA (
        GRAPH(c,quads.map(q=>Trip(q.sub,q.pred,q.obj)).toList)
      )
    )


//    assert { quads.exists(v=>v.sub==page&& v.pred==RDF.TYPE && v.obj== rs / "ResourceShape") }
//    val tq = title.get.toQuads(page)(c)
//    assert { tq.size == 4}
//
//    assert { title.get.toQuads(page).exists(v=>v.sub==page && v.obj==title.get.me) }
//
//
//    assert { quads.exists(v=>v.sub==page&& v.pred==rs / "property" && v.obj==title.get.me) }
//    assert { quads.count(v=>v.sub==title.get.me) == 3 }
//    assert { quads.exists(v=>v.sub==title.get.me && v.pred == rs / "valueType" && v.obj ==  XSD.StringDatatypeIRI )}
//    assert { quads.exists(v=>v.sub==title.get.me && v.pred == rs / "propDefinition" && v.obj ==  WI.pl("title") )}
//    assert { quads.exists(v=>v.sub==title.get.me && v.pred == rs / "occurs" && v.obj == rs / "Exactly-one" )}
//
//
//    assert { quads.exists(v=>v.sub==page&& v.pred==rs / "property" && v.obj==text.get.me) }
//    assert { quads.count(v=>v.sub==text.get.me) == 3 }
//    assert { quads.exists(v=>v.sub==text.get.me && v.pred == rs / "valueType" && v.obj ==  XSD.StringDatatypeIRI )}
//    assert { quads.exists(v=>v.sub==text.get.me && v.pred == rs / "propDefinition" && v.obj ==  WI.pl("text") )}
//    assert { quads.exists(v=>v.sub==text.get.me && v.pred == rs / "occurs" && v.obj ==  rs / "Exactly-one" )}
//
//    assert { quads.exists(v=>v.sub==page&& v.pred==rs / "property" && v.obj==author.get.me) }
//    assert { quads.count(v=>v.sub==author.get.me) == 3 }
//    assert { quads.exists(v=>v.sub==author.get.me && v.pred == rs / "valueType" && v.obj ==  FOAF.PERSON )}
//    assert { quads.exists(v=>v.sub==author.get.me && v.pred == rs / "propDefinition" && v.obj ==  WI.pl("author") )}
//    assert { quads.exists(v=>v.sub==author.get.me && v.pred == rs / "occurs" && v.obj ==  rs / "One-or-many" )}

    "be saved to bigdata" in {

      val db = BigData(true) //cleaning the files and initializing the database

      db.read{con=>
        con.hasStatement(page,RDF.TYPE,rs / "ResourceShape",true,c) shouldEqual false
        con.hasStatement(title.get.me, rs / "propDefinition" ,   WI.pl("title"), true, c) shouldEqual false
        con.hasStatement(title.get.me, rs / "valueType" ,   XSD.StringDatatypeIRI, true, c) shouldEqual false
        con.hasStatement(title.get.me, rs / "occurs" ,   rs / "Exactly-one" , true, c) shouldEqual false

        con.hasStatement(author.get.me, rs / "valueType" ,   FOAF.PERSON, true, c) shouldEqual false
        con.hasStatement(author.get.me, rs / "occurs" ,   rs / "One-or-many" , true, c) shouldEqual false
        true
      }



      val u = db.update(ins.stringValue)
      u.isSuccess shouldEqual true


      db.read{con=>
        con.hasStatement(page,RDF.TYPE,rs / "ResourceShape",true,c) shouldEqual true
        con.hasStatement(title.get.me, rs / "propDefinition" ,   WI.pl("title"), true, c) shouldEqual true
        con.hasStatement(title.get.me, rs / "valueType" ,   XSD.StringDatatypeIRI, true, c) shouldEqual true
        con.hasStatement(title.get.me, rs / "occurs" ,   rs / "Exactly-one" , true, c) shouldEqual true

        con.hasStatement(author.get.me, rs / "valueType" ,   FOAF.PERSON, true, c) shouldEqual true
        con.hasStatement(author.get.me, rs / "occurs" ,   rs / "One-or-many" , true, c) shouldEqual true
        true
      }




      //UNCOMMENT FOLLOWING LINES TO SEE TIMEOUTS
      //val queryFreeze= db.select(wrongQuery)
      db.shutDown() // shutting down

    }
//    "write a file" in {
//
//      val db = BigData(true) //cleaning the files and initializing the database
//      val u = db.update(ins.stringValue)
//      u.isSuccess shouldEqual true
//
//      val file = new File("./sesame/db/test/shapetest.tlt")
//
//      val writer = new PrintWriter(file)
//      val tw = new TurtleWriter(writer)
//
//      tw.startRDF()
//      quads.foreach(q=>tw.handleStatement(q))
//      tw.endRDF()
//
//      db.shutDown()
//
//    }
    "load properties of resource according to the shape" in {

      val db = BigData(true) //cleaning the files and initializing the database

      db.shutDown()

    }
  }



}
