package org.scalax.semweb.parsers
import org.parboiled2.RuleFrame.ZeroOrMore
import org.parboiled2._
import org.scalax.semweb.sparql
import org.scalax.semweb.sparql._
import java.util.Date

class DateParser(val input:ParserInput) extends BasicParser {

  def InputLine:Rule1[Date] = rule {
    NormalDate ~ EOI
  }



  def NormalDate =  rule { DateFromYear | DateFromDay }

  def DateFromYear:Rule1[Date] = rule { (Year ~ Sep ~ ( Month | MonthName) ~ Sep ~ Day) ~>( (y:Int,m:Int,d:Int) => new Date(y,m,d) ) }

  def DateFromDay:Rule1[Date] = rule { (Day ~ Sep ~ ( Month | MonthName) ~ Sep ~ Year)  ~>( (d:Int,m:Int,y:Int) => new Date(y,m,d) ) }


  def Sep = rule { this.anyOf("-;./ ") }

  def Day:Rule1[Int] = rule {
    capture( (1 to 2).times(CharPredicate.Digit) | CharPredicate.Digit) ~>((s:String)=>s.toInt)
  }

  def Month:Rule1[Int] = rule {  capture(2.times(CharPredicate.Digit)) ~>((s:String)=>s.toInt) }

  val months = Map(
    "january"->1,
    "february"->2,
    "march"->3,
    "april"->4,
    "may" ->5,
    "june" ->6,
    "july" ->7,
    "august"->8,
    "september"->9,
    "october"->10,
    "november"->11,
    "december"->12
  )

  def MonthName:Rule1[Int] = rule {
   capture(oneOrMore(CharPredicate.Alpha ))~>{(v:String)=>test(months.contains(v.toLowerCase)) ~ push(months(v.toLowerCase))}
    //capture(2.times(CharPredicate.Digit)) ~>(_.toInt)
  }

  def Year:Rule1[Int] = rule {
    capture( (3 to 4).times(CharPredicate.Digit) ) ~>((s:String)=>s.toInt)
    //|  capture( (1 to 2).times(CharPredicate.Digit) ) ~>( (i:String) =>2000+i.toInt)

  }

}
