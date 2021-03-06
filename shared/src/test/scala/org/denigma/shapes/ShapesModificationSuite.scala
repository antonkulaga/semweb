package org.denigma.shapes
import org.denigma.semweb.rdf.IRI
import org.denigma.semweb.rdf.vocabulary.{FOAF, XSD}
import org.denigma.semweb.shex._
import utest._

object ShapesModificationSuite extends TestSuite{
  val sh = ShapeBuilder(IRI("http://myshape.com")) has
    FOAF.NAME of XSD.StringDatatypeIRI occurs ExactlyOne and
    FOAF.KNOWS oneOf (FOAF.PERSON,FOAF.Group) occurs Plus shape
  val acs = sh.arcRules()

  def tests = TestSuite {
    "Shapes" - {


      "should have updatable AND rules" - {

        val andO = sh.rule
        assert(andO.isInstanceOf[AndRule])
        val and = andO.asInstanceOf[AndRule]
        assert(and.items.size==2)
        val acs = and.items.collect{case r:ArcRule=>r}
        assert(acs.count(f => f.name.matches(FOAF.NAME)) == 1)
        val f1 = acs.find(f => f.name.matches(FOAF.NAME)).get
        assert(f1.occurs == ExactlyOne)
         val and2 =  and.updated(f1.copy(occurs = Plus))
        val arcs2 = and2.items.collect{case r:ArcRule=>r}
        val f2 = arcs2.find(p=>p.name.matches(FOAF.NAME))
        assert(f2.isDefined)
        assert(f2.get.occurs==Plus)

      }
      "should be updatable" - {


        assert(acs.length ==2)
        assert(acs.count(f => f.name.matches(FOAF.NAME)) == 1)

        val fn = acs.find(f => f.name.matches(FOAF.NAME)).get
        assertMatch(fn.name){
          case n:NameTerm if n.property==FOAF.NAME=>
        }


        val pro = ArcRule(fn.id,NameTerm(FOAF.currentProject),ValueType(FOAF.PERSON),ExactlyOne)
        assert(sh.rule.isInstanceOf[AndRule])


        val sh2 = sh.updated(pro)
        val acs2 = sh2.arcRules()
        assert(acs2.length==2)
        assert(acs2.count(f => f.name.matches(FOAF.NAME)) == 0)
        assert(acs2.count(f => f.name.matches(FOAF.currentProject)) == 1)

        val folded = sh2.fold(){
          case arc:ArcRule=> List(arc)
        }
        assert(folded.length==2)
        assert(folded.count(f => f.name.matches(FOAF.NAME)) == 0)
        assert(folded.count(f => f.name.matches(FOAF.currentProject)) == 1)

      }

    }
  }

}
