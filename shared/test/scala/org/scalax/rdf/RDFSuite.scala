package org.scalax.rdf

import org.scalax.semweb.rdf._

import utest._

object RDFSuite extends TestSuite{


  def tests = TestSuite {
    "RDF classes in scalax.rdf should" - {
      "work well with trailing slashes" - {

        IRI("http://foo.com")
        val bar1 = IRI("http://foo.com") / "bar"
        val bar2 = IRI("http://foo.com/") / "bar"
        assert(bar1.stringValue == bar2.stringValue)
        assert(bar1 == bar2)

      }

      "have nice localname and namespace" - {

        val bob = IRI("http://foo.com/resource/Bob")
        assert(bob.namespace=="http://foo.com/resource/")
        assert(bob.localName=="Bob")

        val alice = IRI("http://foo.com/resource#Alice")
        assert(alice.namespace=="http://foo.com/resource#")
        assert(alice.localName=="Alice")


      }



    }
  }

}