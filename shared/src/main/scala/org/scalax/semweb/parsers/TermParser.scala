package org.scalax.semweb.parsers

import org.parboiled2.CharPredicate
import org.parboiled2.RuleFrame.ZeroOrMore
import org.parboiled2._
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.{IRI, BlankNode}
import org.scalax.semweb.{rdf, sparql}
import org.scalax.semweb.sparql._
import java.util.{UUID, Date}
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary._


import shapeless.HNil
import shapeless.ops.hlist.Prepend


trait TermParser extends ExpressionParser {


  protected var _prefixes:Map[String,IRI] = Map(":"->IRI(WI.RESOURCE))
  def prefixes: Map[String, IRI] = _prefixes
  def prefixes_=(value:Map[String,IRI]): Unit = _prefixes=value

  def base = prefixes(":")


  def ECHAR = rule { '\\' ~ anyOf("tbnrf\\\"\'") }


  def PN_CHARS_U = rule {
    PN_CHARS_BASE | '_'
  }


  def PN_CHARS = rule {
    MINUS | CharPredicate.Digit | PN_CHARS_U |  ch('\u00B7') | CharPredicate('\u0300' to '\u036F') | CharPredicate('\u203F' to '\u2040')

  }


  def  PN_PREFIX = rule{ PN_CHARS_BASE ~ zeroOrMore( PN_CHARS | (DOT ~ PN_CHARS)) }


  def PN_CHARS_BASE = rule {
    CharPredicate.Alpha |
      CharPredicate('\u00C0' to '\u00D6') | CharPredicate('\u00D8' to '\u00F6') | CharPredicate('\u00F8' to '\u02FF') |
      CharPredicate('\u0370' to '\u037D') | CharPredicate('\u037F' to '\u1FFF') | CharPredicate('\u200C' to '\u200D') |
      CharPredicate('\u2070' to '\u218F') | CharPredicate('\u2C00' to '\u2FEF') | CharPredicate('\u3001' to '\uD7FF') |
      CharPredicate('\uF900' to '\uFDCF') | CharPredicate('\uFDF0' to '\uFFFD')
  }


  def BooleanLiteral = rule {
    (TRUE | FALSE) ~> (a=>rdf.BooleanLiteral(value = a))
  }

  def IntegerLiteral = rule {
    INTEGER ~> (i=>rdf.IntLiteral(i))
  }

  def DoubleLiteral = rule {
    DECIMAL ~> (d=>rdf.DoubleLiteral(d))

  }

  def BLANK_NODE_LABEL()  = rule{
    "_:" ~ PN_LOCAL ~ WS ~>(s => new BlankNode(s))
  }

  def NEW_BLANK_NODE = rule {
    OPEN_SQUARE_BRACE ~ CLOSE_SQUARE_BRACE ~ push(new BlankNode(UUID.randomUUID().toString))
  }

  def BlankNode = rule {
    BLANK_NODE_LABEL | NEW_BLANK_NODE
  }

  def LANG= rule {
    ignoreCaseWS("LANG")
  }


  def LANGTAG: Rule1[String]  = rule {
     capture(oneOrMore(PN_CHARS_BASE) ~ zeroOrMore(MINUS ~ oneOrMore(PN_CHARS_BASE ~ CharPredicate.Digit) ~ WS))
  }

  def PrefixedName = rule {
    PNAME_LN | PNAME_NS
  }


  def PNAME_NS:Rule1[IRI]= rule{
   optional(capture(PN_PREFIX)) ~>(s=>s.flatMap(s=>prefixes.get(s)).getOrElse(base) ) ~ ChWS(':')
  }

  /**
   * Local names rule (see SPARQL spec)
   * @return
   */
  def PN_LOCAL:Rule1[String]  = rule {
       capture((PN_CHARS_U | CharPredicate.Digit) ~  zeroOrMore(   PN_CHARS | (DOT ~ PN_CHARS) ))   ~ WS
  }


  def PNAME_LN = rule  {
    PNAME_NS ~ PN_LOCAL ~>((pref,local)=> pref / local )
  }



  def IriRef = rule {
    IRI_REF | PrefixedName
  }

  def IRI_REF  = rule {
    LESS_NO_COMMENT ~
      capture(
        zeroOrMore(
        !(
          LESS_NO_COMMENT | GREATER | '"' | OPEN_CURLY_BRACE |
            CLOSE_CURLY_BRACE | '|' | '^' | '\\' | '`' | CharPredicate('\u0000' to '\u0020')

          ) ~ ANY)
      )~>(s=>test(s.contains(":"))~push(IRI(s))) ~ GREATER

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

  def String = rule {
    capture(STRING_LITERAL_LONG1 | STRING_LITERAL1 | STRING_LITERAL_LONG2 | STRING_LITERAL2)
  }


  def REFERENCE = StringWS("^^")

  def DataTypedLiteral = rule {
    String ~ REFERENCE ~IriRef ~>((str,tp)=>new rdf.TypedLiteral(str,tp))
  }

  def LangLiteral = rule {
    String ~ LANGTAG ~>((str,l)=> rdf.StringLangLiteral(str,l))
  }



  def RdfLiteral = rule {
    DataTypedLiteral | LangLiteral | String ~>(rdf.AnyLit(_))
  }


}