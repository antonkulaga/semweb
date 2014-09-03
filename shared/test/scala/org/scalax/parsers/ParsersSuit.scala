package org.scalax.parsers

import java.util.Date

import org.scalax.semweb.parsers.DateParser
import org.scalax.semweb.parsers.sample.Calculator
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


    }


  }

}

