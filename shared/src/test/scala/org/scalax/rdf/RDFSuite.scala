package org.scalax.rdf

import java.util.Date

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


      "pretty print data literal" - {
        val dt = new DateLiteral(new Date(2014,10,14))
        assert(dt.label=="2014-10-14")
      }

      "support Time Literals" - {
        val date = new Date(2014,10,14)
        date.setHours(0)
        date.setMinutes(10)
        date.setSeconds(30)
        val dt = DateTimeLiteral(date)
        val sp = dt.label.split('T')
        assert(sp.size==2)
        val dst = sp.head
        assert(dst=="2014-10-14")
        val ts = sp.tail.head
        assert(ts.contains("00:10:30"))
      }



    }
  }

}