package org.denigma.rdf

import org.denigma.semweb.rdf.{Quad, AnyLit, IRI}
import utest._
import utest.framework.TestSuite

/**
 * Quad/Triplet related features
 */
object QuadTripletSuite extends TestSuite{

  val subject = IRI("http://foo.bar")
  implicit val context = IRI("http://in.bar.we.trust")

  val p1 = IRI("http://has_some_property1")
  val p2 = IRI("http://has_some_property2")


  val o1 = IRI("http://object1.com")
  val o2 = IRI("http://object2.com")

  val l1 = AnyLit("object1.com")
  val l2 = AnyLit("object2.com")

  val p1o1Quad = Quad(subject,p1,o1,context)
  val p1o2Quad = Quad(subject,p1,o2,context)
  val p2o1Quad = Quad(subject,p2,o1,context)
  val p2o2Quad = Quad(subject,p2,o2,context)



  def tests = TestSuite {
    "quads in denigma should" - {

      "compare well to each other" - {


        val p1o1Quad = Quad(subject,p1,o1,context)
        val p1o1Quad_ = Quad(subject,p1,o1,context)

        assert(p1o1Quad==p1o1Quad_)

        val p1o2Quad = Quad(subject,p1,o2,context)
        val p2o1Quad = Quad(subject,p2,o1,context)
        val p2o2Quad = Quad(subject,p2,o2,context)

        assert(p1o1Quad==p1o1Quad_)
        //TODO: finish test

      }


    }
  }

}