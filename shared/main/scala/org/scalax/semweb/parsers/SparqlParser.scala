package org.scalax.semweb.parsers
import org.parboiled2.RuleFrame.ZeroOrMore
import org.parboiled2._
import org.scalax.semweb.sparql
import org.scalax.semweb.sparql._
import java.util.Date

class SelectParser(input: ParserInput) extends ExpressionParser(input) {

  def InputLine = rule {
    Query ~ EOI
  }

  def Query = rule{
      WS ~  Prologue ~ SelectQuery | ConstructQuery | DescribeQuery | AskQuery
  }

  def Prologue = rule{
    optional( this.BaseDecl ) ~ this.zeroOrMore(this.PrefixDecl)
  }

  def PrefixDecl = rule {
    ""
  }

  def BaseDecl = rule {
    this.Base
  }

  def Base = rule {
    ignoreCaseWS("BASE")
  }


  def SelectQuery = rule {
    this.ignoreCaseWS("SELECT")
  }

  def ConstructQuery = rule {
    this.ignoreCaseWS("CONSTRUCT")
  }

  def DescribeQuery = rule {
    this.ignoreCaseWS("DESCRIBE")
  }

  def AskQuery = rule {
    this.ignoreCaseWS("ASK")
  }

  def BLANK_NODE_LABEL()  = rule{
    "_:" ~ PN_LOCAL ~ WS
  }


  def Var1 =  rule{ ch('?') ~ VarName ~ WS  }

  def Var2 = rule{ ch('$') ~ VarName ~ WS  }

  def LangTag = rule {"@" ~ oneOrMore(PN_CHARS_BASE) ~ zeroOrMore(MINUS ~ oneOrMore(PN_CHARS_BASE ~ CharPredicate.Digit) ~ WS)}


//  public Rule STRING_LITERAL1() {
//    return Sequence("'", ZeroOrMore(FirstOf(Sequence(TestNot(FirstOf("'",
//      '\\', '\n', '\r')), ANY), ECHAR())), "'", WS());
//  }

//  public Rule STRING_LITERAL2() {
//    return Sequence('"', ZeroOrMore(FirstOf(Sequence(TestNot(AnyOf("\"\\\n\r")), ANY), ECHAR())), '"', WS());
//  }

//  public Rule STRING_LITERAL_LONG1() {
//    return Sequence("'''", ZeroOrMore(Sequence(
//      Optional(FirstOf("''", "'")), FirstOf(Sequence(TestNot(FirstOf(
//        "'", "\\")), ANY), ECHAR()))), "'''", WS());
//  }
//
//  public Rule STRING_LITERAL_LONG2() {
//    return Sequence("\"\"\"", ZeroOrMore(Sequence(Optional(FirstOf("\"\"", "\"")),
//      FirstOf(Sequence(TestNot(FirstOf("\"", "\\")), ANY), ECHAR()))), "\"\"\"", WS());
//  }


  def ECHAR() = rule { '\\' ~ anyOf("tbnrf\\\"\'") }

  def PN_CHARS_U = rule {
    PN_CHARS_BASE | '_'
  }

  def VarName = rule {
    PN_CHARS_U | CharPredicate.Digit | zeroOrMore(
    PN_CHARS_U | CharPredicate.Digit | CharPredicate('\u0300' to '\u036F') | CharPredicate('\u203F' to '\u2040')
    )
  }

  def PN_CHARS = rule {
    MINUS | CharPredicate.Digit | PN_CHARS_U |  ch('\u00B7') | CharPredicate('\u0300' to '\u036F') | CharPredicate('\u203F' to '\u2040')

  }


  def  PN_PREFIX = rule{ PN_CHARS_BASE ~ optional(zeroOrMore( PN_CHARS | (DOT ~ PN_CHARS))) }

  def PN_LOCAL  = rule {
    (PN_CHARS_U | CharPredicate.Digit) ~ optional(  zeroOrMore(   PN_CHARS | (DOT ~ PN_CHARS) )
    ) ~ WS
  }

  def PN_CHARS_BASE = rule {
    CharPredicate.Alpha |
      CharPredicate('\u00C0' to '\u00D6') | CharPredicate('\u00D8' to '\u00F6') | CharPredicate('\u00F8' to '\u02FF') |
      CharPredicate('\u0370' to '\u037D') | CharPredicate('\u037F' to '\u1FFF') | CharPredicate('\u200C' to '\u200D') |
      CharPredicate('\u2070' to '\u218F') | CharPredicate('\u2C00' to '\u2FEF') | CharPredicate('\u3001' to '\uD7FF') |
      CharPredicate('\uF900' to '\uFDCF') | CharPredicate('\uFDF0' to '\uFFFD')
  }


  //def DIGITS = rule { oneOrMore(CharPredicate.Digit) }

  def COMMENT = rule {
    "#" ~ this.zeroOrMore(EOL | (ANY ~ EOL) )
  }



  def REFERENCE = StringWS("^^")



}

