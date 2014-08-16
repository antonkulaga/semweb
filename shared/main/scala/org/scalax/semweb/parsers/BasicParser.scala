package org.scalax.semweb.parsers
import org.parboiled2.RuleFrame.ZeroOrMore
import org.parboiled2._
import org.scalax.semweb.sparql
import org.scalax.semweb.sparql._
import java.util.Date

class BasicParser(val input: ParserInput) extends Parser{
  def ChWS(s:Char) = rule {
    this.ch(s) ~ WS
  }

  def StringWS(string:String) = rule{ string ~ WS }

  def WS = rule { zeroOrMore(" ") }

  def ignoreCaseWS(string:String) = rule { ignoreCase(string) ~ WS }

}

class ExpressionParser(input:ParserInput) extends BasicParser(input)
{

  def TRUE = rule {
    ignoreCaseWS("TRUE")
  }

  def FALSE = rule{
    ignoreCaseWS("FALSE")
  }

  def INTEGER = rule{
    oneOrMore(CharPredicate.Digit)~WS
  }

  def INTEGER_POSITIVE()  = rule {
    PLUS ~ INTEGER
  }

  def INTEGER_NEGATIVE()  = rule {
    MINUS ~ INTEGER
  }

  def DECIMAL = rule {
    (
      oneOrMore(CharPredicate.Digit) ~ DOT ~ zeroOrMore(CharPredicate.Digit) |
    DOT ~ oneOrMore(CharPredicate.Digit)
    ) ~ WS
  }

  def DECIMAL_POSITIVE = rule {
    PLUS ~ DECIMAL
  }

  def DECIMAL_NEGATIVE = rule {
    MINUS ~ DECIMAL
  }


  def DOUBLE() = rule{ //todo: rewrite
    (
      oneOrMore(CharPredicate.Digit) ~ DOT ~ zeroOrMore(CharPredicate.Digit) ~  EXPONENT |
      DOT ~ oneOrMore(CharPredicate.Digit) ~ EXPONENT |
        oneOrMore(CharPredicate.Digit) ~ EXPONENT
      ) ~ WS
  }


  def EXPONENT = rule{
    ignoreCase('e') ~ optional( PLUS | MINUS ) ~  oneOrMore(CharPredicate.Digit)
  }


  def EOL = rule { anyOf("\n\r") }

  def LESS_EQUAL = StringWS("<=")

  def GREATER_EQUAL = StringWS(">=")


  def NOT_EQUAL = StringWS("!=")

  def AND = rule {
    StringWS("&")
  }

  def OR = rule {
    StringWS("||")
  }

  def OPEN_BRACE = this.ChWS('{')

  def  CLOSE_BRACE = this.ChWS('}')


  def OPEN_CURLY_BRACE = this.ChWS('{')

  def  CLOSE_CURLY_BRACE = this.ChWS('}')


  def OPEN_SQUARE_BRACE = this.ChWS('[')

  def CLOSE_SQUARE_BRACE = this.ChWS(']')

  def SEMICOLON = this.ChWS(';')

  def DOT = this.ChWS('.')


  def PLUS = this.ChWS('!')


  def MINUS = this.ChWS('-')


  def ASTERIX = this.ChWS('*')

  def COMMA = this.ChWS(',')


  def NOT = this.ChWS('!')

  def DIVIDE = this.ChWS('/')

  def EQUAL = this.ChWS('=')

  def LESS = this.ChWS('<')

  def LESS_NO_COMMENT  = rule {
    ch('<') ~ zeroOrMore(WS_NO_COMMENT)
  }

  def WS_NO_COMMENT  = rule {
    ch(' ') | ch('\t') | ch('\f') | EOL
  }

  def GREATER = this.ChWS('>')

}
