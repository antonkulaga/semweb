package org.scalax.semweb.rdf

import java.util.Date

import org.scalax.semweb.parsers.DateParser
import org.scalax.semweb.rdf.vocabulary.XSD

import scala.util.Try

//
//object Lit {
//
//  def apply(string:String) = if(!string.contains("^^")) AnyLit(string) else
//    string.substring(string.indexOf("^^"),string.length-1)
//  match
//  {
//    case str
//    case str if
//
//  }
//
//  def apply(content:String,dataType:IRI) = dataType match {
//    case XSD.BooleanDatatypeIRI=>BooleanLiteral(content.toBoolean)
//    case XSD.DecimalDatatypeIRI=>DecimalLiteral(BigDecimal)
//
//
//  }
//}

//
trait Lit extends RDFValue {

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

  override val label = value.toString

  override def stringValue : String = label //"\"" + label + "\""
}

abstract class DatatypeLiteral(val label : String, val dataType : IRI) extends Lit {

  override def equals(that: Any): Boolean = that match  {

    case l:DatatypeLiteral=>l.label==label && l.dataType ==this.dataType
    case _=>false

  }


  override def stringValue : String = if(dataType!=null) "\"" + label + "\"^^" + "<"+dataType.stringValue+">" else
   label// "\"" + label + "\""

}

case class TypedLiteral( content : String,  tp : IRI) extends DatatypeLiteral(content,tp)

// It should be better to inherit from DatatypeLiteral,
// but case-to-case inheritance is prohibited in Scala
case class IntLiteral(value: Int) extends  DatatypeLiteral(value.toString,XSD.IntegerDatatypeIRI)
{
  override def stringValue = value.toString


  override def equals(that: Any): Boolean = that match  {

    case l:DatatypeLiteral=>l.label==label && (l.dataType ==this.dataType || l.dataType == XSD.IntegerDatatypeIRI)
    case _=>false

  }
}

//case class LongLiteral(value: Long) extends  DatatypeLiteral(value.toString,XSD.LongDatatypeIRI)

case class DecimalLiteral(value: BigDecimal) extends DatatypeLiteral(value.toString(),XSD.DecimalDatatypeIRI)
{
  override def stringValue = value.toString
}

case class DoubleLiteral(value:Double) extends DatatypeLiteral(value.toString,XSD.DoubleDatatypeIRI)
{
  override def stringValue = value.toString
}

case class StringLiteral(text: String) extends DatatypeLiteral(text,XSD.StringDatatypeIRI){

  override def equals(that: Any): Boolean = that match  {

    case l:DatatypeLiteral=>l.label==label && (l.dataType ==this.dataType || l.dataType==null)
    case other:Lit=>other.label==text
    case _=>false

  }
}

case class StringLangLiteral(text: String, lang : String) extends DatatypeLiteral(text,XSD.StringDatatypeIRI) {

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


case class BooleanLiteral(value:Boolean) extends DatatypeLiteral(value.toString,XSD.BooleanDatatypeIRI)
{
  override def stringValue = value.toString
}

object DateTimeFormats{

  /**
   * To port day and month
   * @param int
   * @return
   */
  protected def format2(int:Int) = int match
  {
    case i if i < -9 | i > 9 => i.toString
    case i if i <0 =>"-0"+Math.abs(i)
    case i =>"0"+ i.toString
  }

  def dateFormat(value:Date):String = {
    val (year,month,date) = (value.getYear+1900,value.getMonth+1,value.getDate)
    s"${year}-${format2(month)}-${format2(date)}"
  }

  def timeFormat(value:Date):String = {
/*    val offset = value.getTimezoneOffset
    val ho: Int = offset / 60
    val min = offset % 60
    val strOffset: String = if(offset==0) "" else (if(offset>0) "+" else "")+format2(ho)+":"+format2(min)
    s"${format2(value.getHours)}:${format2(value.getMinutes)}:${format2(value.getSeconds)}$strOffset"*/
    s"${format2(value.getHours)}:${format2(value.getMinutes)}:${format2(value.getSeconds)}"
  }

  def dateTimeFormat(value:Date) = {
    dateFormat(value)+"T"+timeFormat(value)
  }

  def parseDate(value:String) =  new DateParser(value).NormalDate.run()
}

case class DateLiteral(value:Date) extends DatatypeLiteral(DateTimeFormats.dateFormat(value),XSD.Date) {

  override def equals(that: Any): Boolean = that match  {
    case DateLiteral(d)=>value==d
    case l:DatatypeLiteral=>l.label==label && l.dataType ==this.dataType
    case _=>false
  }
}

case class DateTimeLiteral(value:Date) extends DatatypeLiteral(DateTimeFormats.dateTimeFormat(value),XSD.DateTime) {

  override def equals(that: Any): Boolean = that match  {
    case DateTimeLiteral(d)=>value==d
    case l:DatatypeLiteral=>l.label==label && l.dataType ==this.dataType
    case _=>false
  }
}