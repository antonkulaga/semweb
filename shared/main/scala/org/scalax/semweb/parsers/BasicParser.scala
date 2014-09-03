package org.scalax.semweb.parsers
import org.parboiled2.RuleFrame.ZeroOrMore
import org.parboiled2._
import org.scalax.semweb.sparql
import org.scalax.semweb.sparql._
import java.util.Date

trait BasicParser extends Parser{


  def ChWS(s:Char) = rule {
    this.ch(s) ~ WS
  }

  def StringWS(string:String) = rule{ string ~ WS }

  def WS = rule { zeroOrMore(" ") }

  def ignoreCaseWS(string:String) = rule { ignoreCase(string) ~ WS }

  def EOL = rule { anyOf("\n\r") }
}


trait NumericParser extends BasicParser {

  def TRUE = rule {
    ignoreCaseWS("true")~push(true)
  }

  def FALSE = rule{
    ignoreCaseWS("false")~push(false)
  }

  def INTEGER_NEGATIVE= rule {
    MINUS ~ INTEGER_UNSIGNED ~>(i=>0-i)
  }

  def INTEGER_UNSIGNED: Rule1[Int] = rule{
    capture(oneOrMore(CharPredicate.Digit))~>(v=>v.toInt)~WS
  }

  def INTEGER = rule {
    INTEGER_UNSIGNED | INTEGER_NEGATIVE
  }

  /**
   * Is in fact double as it is easier to go this way
   * @return
   */
  def DECIMAL_UNSIGNED = rule {
    (
      ( capture(oneOrMore(CharPredicate.Digit))~ DOT ~ capture(zeroOrMore(CharPredicate.Digit)) ~>((a,b)=> (a+"."+b).toDouble) ) |
      ( DOT ~ capture(oneOrMore(CharPredicate.Digit)) ~>(b=>("."+b).toDouble ) )
    ) ~ WS

  }

  def DECIMAL_NEGATIVE = rule {
    MINUS ~ DECIMAL_UNSIGNED ~>(d=>0.0-d)
  }

  def DECIMAL = rule {
    DECIMAL_UNSIGNED | DECIMAL_NEGATIVE
  }


  def PLUS = this.ChWS('!')


  def MINUS = this.ChWS('-')


  def DOT = this.ChWS('.')

}

trait ExpressionParser extends NumericParser {


  def EXPONENT = rule{
    ignoreCase('e') ~ optional( PLUS | MINUS ) ~  oneOrMore(CharPredicate.Digit)
  }



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


  def ASTERISK = this.ChWS('*')

  def COMMA = this.ChWS(',')


  def NOT = this.ChWS('!')

  def DIVIDE = this.ChWS('/')

  def EQUAL = this.ChWS('=')

  def LESS = this.ChWS('<')

  def COMMENT = rule {
    "#" ~ this.zeroOrMore(EOL | (ANY ~ EOL) )
  }


  def LESS_NO_COMMENT  = rule {
    ch('<') ~ zeroOrMore(WS_NO_COMMENT)
  }

  def WS_NO_COMMENT  = rule {
    ch(' ') | ch('\t') | ch('\f') | EOL
  }

  def GREATER = this.ChWS('>')

}




