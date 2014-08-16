package org.scalax.semweb.parsers
import org.parboiled2.RuleFrame.ZeroOrMore
import org.parboiled2._
import org.scalax.semweb.sparql
import org.scalax.semweb.sparql._
import java.util.Date


class Calculator(val input: ParserInput) extends Parser {
  def InputLine = rule { Expression ~ EOI }

  def Expression: Rule1[Int] = rule {
    Term ~ zeroOrMore(
      '+' ~ Term ~> ((_: Int) + _)
        | '-' ~ Term ~> ((_: Int) - _))
  }

  def Term = rule {
    Factor ~ zeroOrMore(
      '*' ~ Factor ~> ((_: Int) * _)
        | '/' ~ Factor ~> ((_: Int) / _))
  }

  def Factor = rule { Number | Parens }

  def Parens = rule { '(' ~ Expression ~ ')' }

  def Number = rule { capture(Digits) ~> (_.toInt) }

  def Digits = rule { oneOrMore(CharPredicate.Digit) }
}
