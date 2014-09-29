package picklers

import java.util.Date

import org.scalajs.spickling.playjson._
import org.scalax.semweb.picklers.SemanticRegistry
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{FOAF, XSD}
import org.scalax.semweb.shex._
import play.api.libs.json.JsValue
import utest._
import utest.framework.TestSuite



object PlayPicklingSuite extends TestSuite{

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
        assert(a.name.asInstanceOf[NameTerm].property == FOAF.NAME)

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
        assert(a.name.asInstanceOf[NameTerm].property == FOAF.NAME)

      }

      "pickle rdf values" -{

        def generate(value:Int): List[Map[String, RDFValue]] = {
          val obj:RDFValue = value match {
            case 0=> IRI("http://Object")
            case 1=>StringLiteral("StringLiteral")
            case 2=>StringLangLiteral("StringLangLiteral","ru")
            case 3=>AnyLit("AnyLiteral")
            case 4=> DoubleLiteral(20.10)
            case 5=>IntLiteral(20)
            case 6=>BlankNode("blankmeup")
            case 7=>  org.scalax.semweb.rdf.DateLiteral(new Date())
            case 8=> BooleanLiteral(true)
            case 9=> org.scalax.semweb.rdf.DecimalLiteral(10.1)
          }


          List(Map("subject"->IRI("http://Subject"),"property"->IRI("http://property"),"object"->obj))

        }
        for{i <-0 until 9}
        {
          val g =  generate(i)
          assert(SemanticRegistry.unpickle(SemanticRegistry.pickle(g))==g)
        }
      }

      "pickle tuples" -{
        val g2 = (IRI("http://one"),IRI("http://two"))

        assert(SemanticRegistry.unpickle(SemanticRegistry.pickle(g2))==g2)
        val g3 = (IRI("http://one"),IRI("http://two"),IRI("http://three"))
        assert(SemanticRegistry.unpickle(SemanticRegistry.pickle(g3))==g3)

        val g4 = (IRI("http://one"),IRI("http://two"),IRI("http://three"),IRI("http://four"))
        assert(SemanticRegistry.unpickle(SemanticRegistry.pickle(g4))==g4)

        val mixed = (g2,g3,g3)
        assert(SemanticRegistry.unpickle(SemanticRegistry.pickle(mixed))==mixed)

      }

      "pickle options" -{
        val is:Option[IRI] = Some(IRI("http://exists"))
        assert(SemanticRegistry.unpickle(SemanticRegistry.pickle(is))==Some(IRI("http://exists")))

        val non:Option[IRI] = None

        assert(SemanticRegistry.unpickle(SemanticRegistry.pickle(non))==None)

      }

      "pickle maps" -{
        val mp = Map("one"->IRI("http://one"),"two"->IRI("http://two"))
        assert(SemanticRegistry.unpickle(SemanticRegistry.pickle(mp))==mp)


      }

      "test null" -{
        val n:Map[String,String] = null //test for null value

        val p = SemanticRegistry.pickle(n)
        assert(SemanticRegistry.unpickle(p)==null)

      }

    }
  }


}
