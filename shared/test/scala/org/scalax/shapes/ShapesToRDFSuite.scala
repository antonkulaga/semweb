package org.scalax.shapes

import org.scalax.semweb.rdf.{LongLiteral, AnyLit, Quad, IRI}

import utest._
import org.scalax.semweb.shex._
import org.scalax.semweb.rdf.vocabulary._

object ShapesToRDFSuite extends TestSuite{

  def tests = TestSuite{

    val subject = IRI("http://foo.bar")
    implicit val context = IRI("http://in.bar.we.trust")

    val core = "http://open-services.net/ns/core#"

    val occurs = IRI(core+"occurs")
    val minOccurs = IRI(core+"minoccurs")
    val maxOccurs = IRI(core+"maxoccurs")
    val once = IRI(core+"Exactly-one")
    val oneOrMany = IRI(core+"One-or-many")
    val zeroOrMany = IRI(core+"Zero-or-many")
    val zeroOrOne = IRI(core+"Zero-or-one")

   // val one = IRI(core+"One")


    "quads conversions" - {

      "should work well for cadinality constrains:" - {
        "ExactlyOne and Plus" - {
          val q1 = ExactlyOne.toQuads(subject).head

          assert( q1.sub == subject)
          assert( q1.pred == occurs)
          assert( q1.obj == once)
          assert( q1.cont == context)

          val q2 = Plus.toQuads(subject).head

          assert( q2.sub == subject)
          assert( q2.pred == occurs)
          assert( q2.obj == oneOrMany)
          assert( q2.cont == context)
        }

        "zero, zero-one and opt constrains" - {
          val q3 = Star.toQuads(subject).head

          assert( q3.sub == subject)
          assert( q3.pred == occurs)
          assert( q3.obj == zeroOrMany)
          assert( q3.cont == context)

          val q4 = Opt.toQuads(subject).head

          assert( q4.sub == subject)
          assert( q4.pred == occurs)
          assert( q4.obj == zeroOrOne)
          assert( q4.cont == context)
        }

        "bounded constrains" - {
          val q5: Set[Quad] = Cardinality(2,3).toQuads(subject)
          val q = q5.toList
          assert(q.size==2)
          assert( q(0) == Quad(subject,minOccurs,LongLiteral(2),context) )
          assert( q(1) == Quad(subject,maxOccurs,LongLiteral(3),context) )
        }
      }

      "should write simple shape" - {
        val name: IRI = WI / "name" iri

        //ArcRule(Some(IRILabel(name)),NameTerm(FOAF.KNOWS),ValueReference(),ExactlyOne,)

      }

    }


  }

}

