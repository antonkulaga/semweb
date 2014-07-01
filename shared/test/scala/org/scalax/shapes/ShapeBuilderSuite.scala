package org.scalax.shapes
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.{FOAF, XSD}
import org.scalax.semweb.shex.{AndRule, ArcRule, ExactlyOne, NameTerm, Plus, Shape, ShapeBuilder, _}
import utest._

object ShapeBuilderSuite extends TestSuite{

  def tests = TestSuite{


    "Shape building" - {

      object shape extends ShapeBuilder(IRI("http://myshape.com"))
      shape has FOAF.NAME of XSD.StringDatatypeIRI occurs ExactlyOne
      shape has FOAF.KNOWS oneOf (FOAF.PERSON,FOAF.Group) occurs Plus
      val res: Shape = shape.result
      assert(res.rule.isInstanceOf[AndRule])
      val and = res.rule.asInstanceOf[AndRule]
      assert(and.conjoints.size==2)
      val rules = and.conjoints.collect{case arc:ArcRule=>arc}

      assert{ rules.size == 2 }
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
            case ValueSet(vs)=>vs.exists(v=>v==FOAF.PERSON) && vs.exists(v=>v==FOAF.Group) && r.occurs == Plus
            case _ =>false
          }
          case _=>false

        }) )



    }



  }

}

