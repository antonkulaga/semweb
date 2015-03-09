package org.scalax

import org.parboiled2.{CharPredicate, ParserInput, Rule}
import org.scalax.semweb.parsers.sample.BasicElementsParser
import org.scalax.semweb.rdf
import org.scalax.semweb.rdf.IRI
import shapeless.{::, HNil}


class TestRdfTermParser(input:ParserInput) extends BasicElementsParser(input)
{

  def  RdfTerm = rule {  IriRef | RdfLiteral | NumericLiteral |      BooleanLiteral | BlankNode }

  def BlankNode = rule {
    BLANK_NODE_LABEL | (OPEN_SQUARE_BRACE ~ CLOSE_SQUARE_BRACE)
  }



  def RdfLiteral = rule {
    //String ~ optional( LANGTAG | REFERENCE ~IriRef)
    String
  }


  def NumericLiteral = rule {
    NumericLiteralUnsigned | NumericLiteralPositive | NumericLiteralNegative
  }

  def NumericLiteralUnsigned= rule{
    DOUBLE | DECIMAL | INTEGER
  }

  def NumericLiteralPositive = rule {
    DOUBLE_POSITIVE | DECIMAL_POSITIVE | INTEGER_POSITIVE
  }

  def NumericLiteralNegative = rule {
    DOUBLE_NEGATIVE | DECIMAL_NEGATIVE |  INTEGER_NEGATIVE
  }


  def BooleanLiteral = rule {
    (TRUE~push(rdf.BooleanLiteral(value = true))) | (FALSE~push(rdf.BooleanLiteral(false)))
  }

  def String = rule {
    STRING_LITERAL_LONG1 | STRING_LITERAL1 | STRING_LITERAL_LONG2 | STRING_LITERAL2
  }


  def IriRef = rule {
    IRI_REF | PrefixedName
  }

  def PrefixedName = rule {
    PNAME_LN | PNAME_NS
  }



  def PNAME_NS = rule{
    optional(PN_PREFIX) ~ ChWS(':')
  }

  def PNAME_LN = rule  {
    PNAME_NS ~ PN_LOCAL
  }

  def LANG= rule {
    ignoreCaseWS("LANG")
  }
  def BLANK_NODE_LABEL()  = rule{
    capture("_:" ~ PN_LOCAL)~>(v=>rdf.BlankNode(v)) ~ WS
  }


  def LANGTAG = rule {"@" ~ oneOrMore(PN_CHARS_BASE) ~ zeroOrMore(MINUS ~ oneOrMore(PN_CHARS_BASE ~ CharPredicate.Digit) ~ WS)}


  def IRI_REF: Rule[HNil, ::[IRI, HNil]] = rule{
    LESS_NO_COMMENT ~
      capture(
        zeroOrMore(
          !(
            LESS_NO_COMMENT | GREATER | '"' | OPEN_CURLY_BRACE |
              CLOSE_CURLY_BRACE | '|' | '^' | '\\' | '`' | CharPredicate('\u0000' to '\u0020')

            ) ~ ANY)) ~>(v=>rdf.IRI(v)) ~   GREATER
  }


  def STRING_LITERAL1 = rule {
    "'" ~ zeroOrMore(  (  !("'" | '\\' | '\n' | '\r') ~ ANY) | ECHAR) ~ "'" ~ WS
  }

  def STRING_LITERAL2 = rule {
    '"'~ zeroOrMore( !anyOf("\"\\\n\r") ~ ANY | ECHAR)~ '"'~ WS
  }

  def STRING_LITERAL_LONG1  = rule{
    "'''" ~ zeroOrMore(   optional( "''" | "'") ~   !( "'" | "\\" ) ~ ANY | ECHAR) ~ "'''" ~ WS
  }


  def STRING_LITERAL_LONG2 = rule {
    "\"\"\"" ~ zeroOrMore(optional("\"\"" |  "\"") ~ !("\"" | "\\") ~ ANY | ECHAR) ~ "\"\"\""~ WS
  }


  def ECHAR = rule { '\\' ~ anyOf("tbnrf\\\"\'") }


  def PN_CHARS_U = rule {
    PN_CHARS_BASE | '_'
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


  def REFERENCE = StringWS("^^")
}
