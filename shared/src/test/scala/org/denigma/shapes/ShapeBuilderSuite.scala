package org.denigma.shapes
import org.denigma.semweb.rdf.IRI
import org.denigma.semweb.rdf.vocabulary.{FOAF, XSD}
import org.denigma.semweb.shex._
import utest._

object ShapeBuilderSuite extends TestSuite{

  def tests = TestSuite{


    "Shape building" - {

      val builder = ShapeBuilder(IRI("http://myshape.com")) has
        FOAF.NAME of XSD.StringDatatypeIRI occurs ExactlyOne and
        FOAF.KNOWS oneOf (FOAF.PERSON,FOAF.Group) occurs Plus

      val res: Shape = builder.shape
      assert(res.rule.isInstanceOf[AndRule])
      val and = res.rule.asInstanceOf[AndRule]
      assert(and.conjoints.size==2)
      val rules = and.conjoints.collect{case arc:ArcRule=>arc}

      assert{ rules.size == 2 }
      rules.foreach(r=>println("RULE:"+r))
      assert{ rules.exists( r=> r.name == NameTerm(FOAF.NAME) ) }
      assert ( rules.exists(r=>
          r.name match {
            case NameTerm(uri) if uri==FOAF.NAME=>r.value match {
              case ValueType(iri)=>iri == XSD.StringDatatypeIRI && r.occurs == ExactlyOne
              case _ =>false
            }
            case _=>false

          }) )

      assert ( rules.exists(r=>
        r.name match {
          case NameTerm(uri) if uri==FOAF.KNOWS=> r.value match {
            case ValueSet(vs)=>vs.contains(FOAF.PERSON) && vs.contains(FOAF.Group) && r.occurs == Plus
            case _ =>false
          }
          case _=>false

        }) )



    }

    "testing set of values" - {
      val dnaBase =  IRI("http://denigma.org/resource/DNA_base")
      val de = IRI("http://denigma.org/resource/")
      val valueClass = ValueSet(Set(de / "A", de / "T", de / "G", de /"C"))
      val shape =  ShapeBuilder(IRI("http://myshape.com")) has dnaBase from(de / "A", de / "T", de / "G", de /"C")

      val res = shape.shape

      assert(res.arcSorted.head.value.isInstanceOf[ValueSet])
      assert(res.arcSorted.head.value==valueClass)


    }



  }

}

