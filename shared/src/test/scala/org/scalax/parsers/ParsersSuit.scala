package org.scalax.parsers

import java.util.Date

import org.scalax.semweb.parsers.DateParser
import org.scalax.semweb.parsers.sample.Calculator
import org.scalax.semweb.rdf.DateTimeFormats
import utest._
import org.scalax.semweb._

import scala.util.Try

object ParsersSuit extends TestSuite{

  def tests = TestSuite{



    "parsers" - {

      "should provide calculations" - {
        val r =  new Calculator("1+1").InputLine.run()
        assert(r.isSuccess)
        assert(r.get==2)

        val r2 =  new Calculator("(2+2)*3+3").InputLine.run()
        assert(r2.isSuccess)
        assert(r2.get==15)
      }

      "should work with dates" - {
        val r = new DateParser("01.02.2010").InputLine.run()
        assert(r.isSuccess)
        val d1: Date = r.get
        assert(d1.getYear==2010)
        assert(d1.getMonth==2)
        assert(d1.getDate==1)
      }


      "support DateParsing" - {
        val do1 = DateTimeFormats.parseDate("2014.10.14")
        assert(do1.isSuccess)
        val d1 = do1.get
        assert(d1.getYear==2014)
        assert(d1.getMonth==10)
        assert(d1.getDate==14)
        DateTimeFormats.parseDate("20014.12.14").isFailure
        DateTimeFormats.parseDate("Tue, 2014.12.14").isFailure
        val do2 = DateTimeFormats.parseDate("14-10-2014")
        assert(do2.isSuccess)
        val d2 = do2.get
        assert(d1.getYear==2014)
        assert(d1.getMonth==10)
        assert(d1.getDate==14)
      }


    }


  }

}

