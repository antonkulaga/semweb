package org.scalax.semweb.rdf

object xsd
{
  val namespace					= "http://www.w3.org/2001/XMLSchema#"
  def +(str:String) = IRI(namespace+str)

}

/*
  val rdftype				= IRI(rdfSyntax + "type")
  val rdfnil					= IRI(rdfSyntax + "nil")
  val rdffirst			    = IRI(rdfSyntax + "first")
  val rdfrest			    = IRI(rdfSyntax + "rest")
 */

object Lit {
  val rdfSyntax				= "http://www.w3.org/1999/02/22-rdf-syntax-ns#"



  val StringDatatypeIRI 		=  xsd + "string"
  val LangStringDatatypeIRI  = xsd + "language"
  val BooleanDatatypeIRI 	= xsd + "boolean"
  val IntegerDatatypeIRI 	= xsd + "integer"
  val LongDatatypeIRI 	= xsd + "long"
  val DoubleDatatypeIRI 		= xsd + "double"
  val DecimalDatatypeIRI 	= xsd + "decimal"

//  val trueLiteral = BooleanLiteral(true)
//  val falseLiteral = BooleanLiteral(false)


}


//
trait Lit extends RDFValue {
  def label : String


  def isLangLiteral = false
  def hasLang(language:String) = false

  override def stringValue : String = "\"" + label + "\""
  override def toString = stringValue

  override def equals(that: Any): Boolean = that match  {

    case l:Lit=>l.label==label
    case _=>false

  }
}

case class AnyLit(value:Any) extends Lit{

  val label = value.toString

  override def stringValue : String = "\"" + label + "\""
}

abstract class DatatypeLiteral(val label : String, val dataType : IRI) extends Lit {

  override def equals(that: Any): Boolean = that match  {

    case l:DatatypeLiteral=>l.label==label && l.dataType ==this.dataType
    case _=>false

  }

  override def stringValue : String = if(dataType!=null) "\"" + label + "\"^^" + "<"+dataType.stringValue+">" else
    "\"" + label + "\""


}

// It should be better to inherit from DatatypeLiteral,
// but case-to-case inheritance is prohibited in Scala
case class IntegerLiteral(value: Integer) extends  DatatypeLiteral(value.toString,Lit.IntegerDatatypeIRI)
case class LongLiteral(value: Long) extends  DatatypeLiteral(value.toString,Lit.LongDatatypeIRI)

case class DecimalLiteral(value: BigDecimal) extends DatatypeLiteral(value.toString(),Lit.DecimalDatatypeIRI)

case class DoubleLiteral(value:Double) extends DatatypeLiteral(value.toString,Lit.DoubleDatatypeIRI)

case class StringLiteral(text: String) extends DatatypeLiteral(text,Lit.StringDatatypeIRI){

  override def equals(that: Any): Boolean = that match  {

    case l:DatatypeLiteral=>l.label==label && (l.dataType ==this.dataType || l.dataType==null)
    case other:Lit=>other.label==text
    case _=>false

  }
}

case class StringLangLiteral(text: String, lang : String) extends DatatypeLiteral(text,Lit.StringDatatypeIRI) {

  override def isLangLiteral = true
  override def hasLang(language:String) = lang.toLowerCase == language
  override def stringValue : String = {
    val lex = "\"" + label + "\""
    if(lang!="")  lex + lang else lex
  }

  override def equals(that: Any): Boolean = that match  {
    case l:StringLangLiteral=>l.label==label && l.lang==lang
    case other:Lit=>other.label==text
    case _=>false

  }



}

case class BooleanLiteral(value:Boolean) extends DatatypeLiteral(value.toString,Lit.BooleanDatatypeIRI)


//case class Lang(lang : String) {
//
//  // This should be the right regular expression for lang.
//  // We don't use this expression because the specification does not also.
//  val langtag_ex : String = "(\\A[xX]([\\x2d]\\p{Alnum}{1,8})*\\z)" +
//    "|(((\\A\\p{Alpha}{2,8}(?=\\x2d|\\z)){1}" +
//    "(([\\x2d]\\p{Alpha}{3})(?=\\x2d|\\z)){0,3}" +
//    "([\\x2d]\\p{Alpha}{4}(?=\\x2d|\\z))?" +
//    "([\\x2d](\\p{Alpha}{2}|\\d{3})(?=\\x2d|\\z))?" +
//    "([\\x2d](\\d\\p{Alnum}{3}|\\p{Alnum}{5,8})(?=\\x2d|\\z))*)" +
//    "(([\\x2d]([a-wyzA-WYZ](?=\\x2d))([\\x2d](\\p{Alnum}{2,8})+)*))*" +
//    "([\\x2d][xX]([\\x2d]\\p{Alnum}{1,8})*)?)\\z"
//
//  // TODO. Specification defines other ways to match languages
//  def matchLanguage(other : Lang) =
//    this.lang.toLowerCase == other.lang.toLowerCase
//
//  override def toString = lang match {
//    case "" => ""
//    case ls => "@" + ls
//  }
//
//}
