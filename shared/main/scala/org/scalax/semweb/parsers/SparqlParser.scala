package org.scalax.semweb.parsers
import org.parboiled2.RuleFrame.ZeroOrMore
import org.parboiled2._
import org.scalax.semweb.sparql
import org.scalax.semweb.sparql._
import java.util.Date

import shapeless.HNil

class SparqlParser(input: ParserInput) extends BasicElementsParser(input) {

  def InputLine = rule { Query ~ EOI }


  def Query = rule {
    WS ~ Prologue ~ (SelectQuery | ConstructQuery | DescribeQuery | AskQuery)
  }

  def Prologue= rule {
    optional(BaseDecl) ~ zeroOrMore(PrefixDecl)
  }

  def BaseDecl= rule { BASE ~ IRI_REF }

  def PrefixDecl= rule {  PREFIX ~ PNAME_NS ~ IRI_REF  }

  def SelectQuery= rule {
    SELECT ~ optional(DISTINCT |   REDUCED) ~   ( oneOrMore(Var) | ASTERISK) ~  zeroOrMore(DatasetClause) ~ WhereClause ~ SolutionModifier
  }

  def ConstructQuery= rule {
    (CONSTRUCT ~ ConstructTemplate ~ zeroOrMore(DatasetClause) ~ WhereClause ~ SolutionModifier)
  }

  def DescribeQuery= rule {
    DESCRIBE ~ (oneOrMore(VarOrIRIref) | ASTERISK) ~ zeroOrMore(DatasetClause) ~  optional(WhereClause) ~ SolutionModifier
  }



  def AskQuery= rule{
    ASK ~ zeroOrMore(DatasetClause) ~ WhereClause
  }

  def DatasetClause= rule{
      FROM ~  (DefaultGraphClause |  NamedGraphClause);
  }

  def DefaultGraphClause= rule{
    SourceSelector
  }

  def NamedGraphClause= rule{
     NAMED ~ SourceSelector
  }

  def SourceSelector= rule{
    IriRef
  }

  def WhereClause= rule{
    optional(WHERE) ~ GroupGraphPattern
  }

  def SolutionModifier= rule{
    optional(OrderClause) ~ optional(LimitOffsetClauses)
  }
  


  def  LimitOffsetClauses= rule{
      (LimitClause ~ optional(OffsetClause) ) |
      (OffsetClause ~ optional(LimitClause))
  }

  def  OrderClause= rule{
     ORDER ~ BY ~ oneOrMore(OrderCondition)
  }

  def  OrderCondition= rule{
      ((ASC | DESC) ~ BrackettedExpression)   | (Constraint | Var )
  }

  def  LimitClause= rule{    LIMIT ~ INTEGER   }

  def  OffsetClause= rule{  OFFSET ~ INTEGER  }

  def  GroupGraphPattern= rule{
    OPEN_CURLY_BRACE ~ optional(TriplesBlock) ~   zeroOrMore(     (GraphPatternNotTriples | Filter) ~
        optional(DOT) ~ optional(TriplesBlock)  ) ~     CLOSE_CURLY_BRACE
  }
  
  
  def TriplesBlock: Rule[HNil, HNil]  = rule {
    TriplesSameSubject ~ optional(DOT ~ optional(TriplesBlock))
  }

  def GraphPatternNotTriples = rule {    OptionalGraphPattern | GroupOrUnionGraphPattern |      GraphGraphPattern   }

  def OptionalGraphPattern: Rule[HNil, HNil]  = rule {
    OPTIONAL ~ GroupGraphPattern
  }

  def GraphGraphPattern: Rule[HNil, HNil]  = rule {
    GRAPH ~ VarOrIRIref ~ GroupGraphPattern
  }

  def GroupOrUnionGraphPattern: Rule[HNil, HNil]  = rule {
    GroupGraphPattern ~ zeroOrMore(UNION ~ GroupGraphPattern)
  }

  def Filter = rule { FILTER ~ Constraint}

  def Constraint = rule {     BrackettedExpression |  BuiltInCall | FunctionCall   }

  def FunctionCall = rule {  IriRef ~ ArgList   }


  def ArgList = rule {
    (OPEN_BRACE ~ CLOSE_BRACE) |    (      OPEN_BRACE ~ Expression ~ zeroOrMore(COMMA ~ Expression) ~ CLOSE_BRACE)
  }

  def ConstructTemplate= rule {
    OPEN_CURLY_BRACE  ~ optional(ConstructTriples) ~  CLOSE_CURLY_BRACE
  }

  def ConstructTriples: Rule[HNil, HNil] = rule {
    TriplesSameSubject ~ optional(DOT ~  optional(ConstructTriples))
  }

  def TriplesSameSubject= rule {
        (VarOrTerm ~ PropertyListNotEmpty) |     ( TriplesNode ~ PropertyList)
  }

  def PropertyListNotEmpty: Rule[HNil, HNil] = rule {
    Verb ~ ObjectList ~ zeroOrMore(SEMICOLON ~  optional(Verb ~ ObjectList))
  }

  def PropertyList= rule {
    optional(PropertyListNotEmpty)
  }

  def ObjectList= rule {
    Object_ ~ zeroOrMore(  COMMA ~ Object_)
  }

  def Object_ = rule { GraphNode   }

  def Verb= rule {  VarOrIRIref | A  }

  def TriplesNode= rule {   Collection | BlankNodePropertyList  }

  def BlankNodePropertyList: Rule[HNil, HNil] = rule {
    OPEN_SQUARE_BRACE ~ PropertyListNotEmpty ~ CLOSE_SQUARE_BRACE
  }

  def Collection: Rule[HNil, HNil] = rule {
    OPEN_BRACE ~  oneOrMore(GraphNode) ~ CLOSE_BRACE
  }

  def GraphNode = rule { VarOrTerm | TriplesNode }


  def VarOrTerm= rule { Var | GraphTerm}

  def VarOrIRIref= rule {  Var | IriRef }

  def  Var: Rule[HNil, HNil] = rule { VAR1 | VAR2 }

  def  GraphTerm = rule {  IriRef | RdfLiteral | NumericLiteral |      BooleanLiteral | BlankNode |  (OPEN_BRACE ~  CLOSE_BRACE)  }

  def Expression: Rule[HNil, HNil]  = { ConditionalOrExpression }

  def ConditionalOrExpression = {
    rule {
      ConditionalAndExpression ~ zeroOrMore(OR ~ ConditionalAndExpression)
    }
  }

  def ConditionalAndExpression = rule{
    ValueLogical ~ zeroOrMore( AND ~ ValueLogical)
  }

  def  ValueLogical = rule{  RelationalExpression }

  def  RelationalExpression = rule{
    NumericExpression ~ optional(//
      (EQUAL ~ NumericExpression) | //
        (NOT_EQUAL ~ NumericExpression) | //
        (LESS ~ NumericExpression) | //
        (GREATER ~ NumericExpression) | //
        (LESS_EQUAL ~ NumericExpression) | //
        (GREATER_EQUAL ~ NumericExpression)  //
    )
  }

  def  NumericExpression = rule { AdditiveExpression }

  def AdditiveExpression= rule {
    MultiplicativeExpression ~
      zeroOrMore(
        (PLUS ~ MultiplicativeExpression) | //
        (MINUS ~ MultiplicativeExpression) | //
        NumericLiteralPositive | NumericLiteralNegative
      )
  }

  def MultiplicativeExpression= rule {
    UnaryExpression ~ zeroOrMore(
      (  ASTERISK ~ UnaryExpression)  | (DIVIDE ~  UnaryExpression)
    )
  }

  def UnaryExpression= rule {
      (NOT ~ PrimaryExpression) |  (PLUS ~    PrimaryExpression) | (MINUS ~ PrimaryExpression) |      PrimaryExpression
  }

  def PrimaryExpression= rule {
    BrackettedExpression | BuiltInCall |    IriRefOrFunction | RdfLiteral | NumericLiteral |
      BooleanLiteral | Var
  }


  def BrackettedExpression  = rule{
    OPEN_BRACE ~ Expression ~ CLOSE_BRACE
  }


 def BuiltInCall = rule {
      (STR ~ OPEN_BRACE ~ Expression ~ CLOSE_BRACE) |
      (LANG ~ OPEN_BRACE ~ Expression ~ CLOSE_BRACE) |
      (LANGMATCHES ~ OPEN_BRACE ~ Expression ~ COMMA ~  Expression ~ CLOSE_BRACE) |
      (DATATYPE ~ OPEN_BRACE ~ Expression ~ CLOSE_BRACE) |
      (BOUND ~ OPEN_BRACE ~ Var ~ CLOSE_BRACE) |
      (SAMETERM ~ OPEN_BRACE ~ Expression ~ COMMA ~ Expression ~ CLOSE_BRACE) |
      (ISIRI ~ OPEN_BRACE ~ Expression ~ CLOSE_BRACE) |
      (ISURI ~ OPEN_BRACE ~ Expression ~ CLOSE_BRACE) |
      (ISBLANK ~ OPEN_BRACE ~ Expression ~ CLOSE_BRACE) |
      (ISLITERAL ~ OPEN_BRACE ~ Expression ~ CLOSE_BRACE) |
      RegexExpression
  }


  def RegexExpression = rule {
    REGEX ~ OPEN_BRACE ~ Expression ~ COMMA ~   Expression ~ optional( COMMA ~ Expression) ~  CLOSE_BRACE
  }



  def IriRefOrFunction = rule{
    IriRef ~ optional(ArgList)
  }

  def RdfLiteral = rule {
    String ~ optional( LANGTAG | REFERENCE ~IriRef)
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
    TRUE | FALSE
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

  def BlankNode = rule {
    BLANK_NODE_LABEL | (OPEN_SQUARE_BRACE ~ CLOSE_SQUARE_BRACE)
  }


  def PNAME_NS = rule{
   optional(PN_PREFIX) ~ ChWS(':')
  }

  def PNAME_LN = rule  {
    PNAME_NS ~ PN_LOCAL
  }


  def BASE= rule {
   ignoreCaseWS("BASE")
  }

  def PREFIX= rule {
   ignoreCaseWS("PREFIX")
  }

  def SELECT= rule {
   ignoreCaseWS("SELECT")
  }

  def DISTINCT= rule {
   ignoreCaseWS("DISTINCT")
  }

  def REDUCED= rule {
   ignoreCaseWS("REDUCED")
  }

  def CONSTRUCT= rule {
   ignoreCaseWS("CONSTRUCT")
  }

  def DESCRIBE= rule {
   ignoreCaseWS("DESCRIBE")
  }

  def ASK= rule {
   ignoreCaseWS("ASK")
  }

  def FROM= rule {
   ignoreCaseWS("FROM")
  }

  def NAMED= rule {
   ignoreCaseWS("NAMED")
  }

  def WHERE= rule {
   ignoreCaseWS("WHERE")
  }

  def ORDER= rule {
   ignoreCaseWS("ORDER")
  }

  def BY= rule {
   ignoreCaseWS("BY")
  }

  def ASC= rule {
   ignoreCaseWS("ASC")
  }

  def DESC= rule {
   ignoreCaseWS("DESC")
  }

  def LIMIT= rule {
   ignoreCaseWS("LIMIT")
  }

  def OFFSET= rule {
   ignoreCaseWS("OFFSET")
  }

  def OPTIONAL= rule {
   ignoreCaseWS("OPTIONAL")
  }

  def GRAPH= rule {
   ignoreCaseWS("GRAPH")
  }

  def UNION= rule {
   ignoreCaseWS("UNION")
  }

  def FILTER= rule {
   ignoreCaseWS("FILTER")
  }

  def A= rule {
    ChWS('a')
  }

  def STR= rule {
   ignoreCaseWS("STR")
  }

  def LANG= rule {
   ignoreCaseWS("LANG")
  }

  def LANGMATCHES= rule {
   ignoreCaseWS("LANGMATCHES")
  }

  def DATATYPE= rule {
   ignoreCaseWS("DATATYPE")
  }

  def BOUND= rule {
   ignoreCaseWS("BOUND")
  }

  def SAMETERM= rule {
   ignoreCaseWS("SAMETERM")
  }

  def ISIRI= rule {
   ignoreCaseWS("ISIRI")
  }

  def ISURI= rule {
   ignoreCaseWS("ISURI")
  }

  def ISBLANK= rule {
   ignoreCaseWS("ISBLANK")
  }

  def ISLITERAL= rule {
   ignoreCaseWS("ISLITERAL")
  }

  def REGEX= rule {
   ignoreCaseWS("REGEX")
  }

  def BLANK_NODE_LABEL()  = rule{
    "_:" ~ PN_LOCAL ~ WS
  }


  def VAR1 =  rule{ ch('?') ~ VarName ~ WS  }

  def VAR2 = rule{ ch('$') ~ VarName ~ WS  }

  def LANGTAG = rule {"@" ~ oneOrMore(PN_CHARS_BASE) ~ zeroOrMore(MINUS ~ oneOrMore(PN_CHARS_BASE ~ CharPredicate.Digit) ~ WS)}


  def IRI_REF  = rule{
      LESS_NO_COMMENT ~
      zeroOrMore(
        !(
         LESS_NO_COMMENT | GREATER | '"' | OPEN_CURLY_BRACE |
        CLOSE_CURLY_BRACE | '|' | '^' | '\\' | '`' | CharPredicate('\u0000' to '\u0020')

      ) ~ ANY) ~   GREATER
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




  def REFERENCE = StringWS("^^")



}

