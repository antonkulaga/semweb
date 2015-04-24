package org.denigma.parsers

import java.util.{Calendar, Date}

import org.denigma.semweb.parsers.DateParser
import org.denigma.semweb.parsers.sample.Calculator
import org.denigma.semweb.rdf.DateTimeFormats
import org.denigma.utils.{DateFixer, DateTester}
import utest._
import org.denigma.semweb._

import scala.util.Try

object ParsersSuit extends TestSuite with DateFixer with DateTester{

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
        this.testDate(d1,2010,2,1)
      }


      "support DateParsing" - {
        val do1 = DateTimeFormats.parseDate("2014.10.14")
        assert(do1.isSuccess)
        val d1 = do1.get
        this.testDate(d1,2014,10,14)
        DateTimeFormats.parseDate("20014.12.14").isFailure
        DateTimeFormats.parseDate("Tue, 2014.12.14").isFailure
        val do2 = DateTimeFormats.parseDate("14-10-2014")
        assert(do2.isSuccess)
        val d2 = do2.get
        this.testDate(d2,2014,10,14)
      }


    }


  }

}

