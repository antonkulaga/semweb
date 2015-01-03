package org.scalax.parsers

import org.parboiled2._
import org.scalax.semweb.parsers.TermParser
import org.scalax.semweb.rdf
import org.scalax.semweb.rdf.{IRI, BlankNode, BooleanLiteral}
import utest._
import utest.framework.TestSuite

import scala.util.{Failure, Success}

object SparqlParserSuit extends TestSuite {
  class TestTermParser(val input:ParserInput) extends TermParser{



  }



  def tests = TestSuite{


    "SPARQL parser" - {

      "should parse boolean literals" - {
        assert(new TestTermParser("true").TRUE.run().isFailure==false)
        //
        //
        assertMatch(new TestTermParser("true").BooleanLiteral.run()){case Success(BooleanLiteral(true))=>}
        assertMatch(new TestTermParser("false").BooleanLiteral.run()){case Success(BooleanLiteral(false))=>}
        assertMatch(new TestTermParser("true ").BooleanLiteral.run()){case Success(BooleanLiteral(true))=>}
        assertMatch(new TestTermParser("TRUE ").BooleanLiteral.run()){case Success(BooleanLiteral(true))=>}
        assertMatch(new TestTermParser("TRUsE ").BooleanLiteral.run()){case Failure(th)=>}
        //assertMatch(new TestTermParser("TRUE BASTARD").BooleanLiteral.run()){case Failure(th)=>}

      }

      "should parse integers" - {
        assert(new TestTermParser("true").TRUE.run().isFailure==false)
        //
        //
        assertMatch(new TestTermParser("1").IntegerLiteral.run()){case Success(rdf.IntLiteral(1))=>}
        assertMatch(new TestTermParser("10 ").IntegerLiteral.run()){case Success(rdf.IntLiteral(10))=>}
        assertMatch(new TestTermParser("-10").IntegerLiteral.run()){case Success(rdf.IntLiteral(-10))=>}

      }

      "should parse double" - {
        assert(new TestTermParser("true").TRUE.run().isFailure==false)
        //
        //
        assertMatch(new TestTermParser("1.2").DoubleLiteral.run()){case Success(rdf.DoubleLiteral(1.2))=>}
        assertMatch(new TestTermParser("10.4 ").DoubleLiteral.run()){case Success(rdf.DoubleLiteral(10.4))=>}
        assertMatch(new TestTermParser("-10.3").DoubleLiteral.run()){case Success(rdf.DoubleLiteral(-10.3))=>}
        assertMatch(new TestTermParser("-.3").DoubleLiteral.run()){case Success(rdf.DoubleLiteral(-.3))=>}

        assertMatch(new TestTermParser("10").DoubleLiteral.run()){case Failure(th)=>}

      }

      "should extract blank node from query" - {
        assert(new TestTermParser("_h:2").BlankNode.run().isFailure)
        assert(new TestTermParser("[hello]").BlankNode.run().isFailure)


        val bno = new TestTermParser("[]").BlankNode.run()
        assert(bno.isSuccess)
        val bn = bno.get
        assert(bn.id!="")
        assert(bn.id!="[]")

        val bno2 = new TestTermParser("_:2").BlankNode.run()
        assert(bno2.isSuccess)
        val bn2 = bno2.get
        assert(bn2.id=="2")



      }

      "should parse <IRI>" - {
        assert(new TestTermParser("<http.google.com.ua>").IRI_REF.run().isFailure)
        assert(new TestTermParser("<http.google.com.ua>").IriRef.run().isFailure)

        assertMatch(new TestTermParser("<http://google.com.ua>").IRI_REF.run()){case Success(IRI("http://google.com.ua"))=>}
        //assertMatch(new TestTermParser("<http://google.com.ua>").IriRef.run()){case Success(IRI("http://google.com.ua"))=>}

      }

      "should parse prefixed names" - {
        val hello = new TestTermParser("hello").PN_LOCAL.run()
        assert(hello.isSuccess)
        val he = hello.get
        assert(he=="hello")


        val ps = new TestTermParser("de:hello")
        ps.prefixes = ps.prefixes + ("de"->IRI("http://denigma.de/resource/"))

        val lno = ps.PNAME_LN.run()
        assert(lno.isSuccess)
        val ln = lno.get
        assert(ln== IRI("http://denigma.de/resource/hello"))
        val dego = ps.PrefixedName.run()
        assert(dego.isSuccess)
        val deg = dego.get
        assert(deg== IRI("http://denigma.de/resource/hello"))

      }


      "should parse literals" - {
        //TODO: write tests
        }



    }
  }
}
