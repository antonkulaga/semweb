package org.scalax.picklers

import java.util.Date

import org.scalajs.spickling.playjson._
import org.scalax.semweb.picklers.SemanticRegistry
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.{FOAF, XSD}
import org.scalax.semweb.shex._
import play.api.libs.json.JsValue
import utest._
import utest.framework.TestSuite


object PicklingSuite extends TestSuite{

  def tests = TestSuite{


    "Picklers" - {

      "should pickle/unpickle Date" - {
        val v: Long = 100000
        val d = new Date(v)
        assert(d.getTime==v)

        val p: JsValue = SemanticRegistry.pickle(d)
        val d2: Date = SemanticRegistry.unpickle(p).asInstanceOf[Date]
        assert(d2 != null)
        assert(d.getTime==d2.getTime)
        assert(d2.getTime==v)


      }

      "should pickle/unplicke Arcs" - {
        object shape extends ShapeBuilder(IRI("http://myshape.com"))

        val ao: Option[ArcRule] =   shape has FOAF.NAME of XSD.StringDatatypeIRI occurs ExactlyOne result
        var a = ao.get
        assert(a.occurs == ExactlyOne)
        assert(a.name.isInstanceOf[NameTerm])
        assert(a.name.asInstanceOf[NameTerm].t == FOAF.NAME)

      }

      "should pickle/unpickle Shape" - {


        object shape extends ShapeBuilder(IRI("http://myshape.com"))
        shape has FOAF.NAME of XSD.StringDatatypeIRI occurs ExactlyOne
        shape has FOAF.KNOWS oneOf (FOAF.PERSON,FOAF.Group) occurs Plus
        val res: Shape = shape.result
        val p: JsValue = SemanticRegistry.pickle(res)
        val sh = SemanticRegistry.unpickle(p).asInstanceOf[Shape]
        assert(sh!=null)
        val rules = sh.arcRules()//(IRI(WI.RESOURCE))
        assert (rules.length ==2)
        val a = rules.head
        assert(a.occurs == ExactlyOne)
        assert(a.name.isInstanceOf[NameTerm])
        assert(a.name.asInstanceOf[NameTerm].t == FOAF.NAME)

      }
    }
  }


}
