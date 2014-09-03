package org.scalax.semweb.parsers
import org.parboiled2.RuleFrame.ZeroOrMore
import org.parboiled2._
import org.scalax.semweb.sparql
import org.scalax.semweb.sparql._
import java.util.Date

class DateParser(val input:ParserInput) extends BasicParser {

  def InputLine = rule {
    NormalDate ~ EOI
  }

  def NormalDate =  rule { (Day ~ Sep ~ ( Month | MonthName) ~ Sep ~ Year) ~>((d,m,y)=> new Date(y,m,d))}//new Date(y,m,d)) }


  def Sep = rule { this.anyOf(";./ ") }

  def Day = rule {
    capture( (1 to 2).times(CharPredicate.Digit) | CharPredicate.Digit) ~>(_.toInt)
  }

  def Month = rule {  capture(2.times(CharPredicate.Digit)) ~>(_.toInt) }

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

  def MonthName = rule {
   capture(oneOrMore(CharPredicate.Alpha ))~>{v=>test(months.contains(v.toLowerCase)) ~ push(months(v.toLowerCase))}
    //capture(2.times(CharPredicate.Digit)) ~>(_.toInt)
  }

  def Year = rule {
    capture( (3 to 4).times(CharPredicate.Digit) ) ~>(_.toInt) |
      capture( (1 to 2).times(CharPredicate.Digit) ) ~>(i=>2000+i.toInt)

  }

}
