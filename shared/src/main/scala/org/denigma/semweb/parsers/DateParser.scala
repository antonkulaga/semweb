package org.denigma.semweb.parsers
import java.util.Date

import org.parboiled2._

class DateParser(val input:ParserInput) extends BasicParser {

  def InputLine:Rule1[Date] = rule {
    NormalDate ~ EOI
  }


  /**
   * Allocates a <code>Date</code> object and initializes it so that
   * it represents midnight, local time, at the beginning of the day
   * specified by the <code>year</code>, <code>month</code>, and
   * <code>date</code> arguments.
   *
   * @param   year    the year minus 1900.
   * @param   month   the month between 0-11.
   * @param   date    the day of the month between 1-31.
   * @see     java.util.Calendar
   * @deprecated As of JDK version 1.1,
   * replaced by <code>Calendar.set(year + 1900, month, date)</code>
   * or <code>GregorianCalendar(year + 1900, month, date)</code>.
   */
  /**
   * creates date by using calendar
   * @param year
   * @param month
   * @param day
   * @return
   */
  protected def date(year:Int,month:Int,day:Int) = {
    new Date(year-1900,month-1,day)
    /*val c = Calendar.getInstance()
    c.set(year,month-1,day)
    new Date(c.getTimeInMillis)*/
  }



  def NormalDate =  rule { DateFromYear | DateFromDay }

  def DateFromYear:Rule1[Date] = rule { (Year ~ Sep ~ ( Month | MonthName) ~ Sep ~ Day) ~>( (y:Int,m:Int,d:Int) => date(y,m,d) ) }

  def DateFromDay:Rule1[Date] = rule { (Day ~ Sep ~ ( Month | MonthName) ~ Sep ~ Year)  ~>( (d:Int,m:Int,y:Int) => date(y,m,d) ) }


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
