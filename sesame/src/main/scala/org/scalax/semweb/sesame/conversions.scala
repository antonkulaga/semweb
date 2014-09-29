package org.scalax.semweb.sesame

import java.util.GregorianCalendar
import org.scalax.semweb.rdf._
import org.openrdf.model.impl.{LiteralImpl, BNodeImpl, URIImpl}
import org.openrdf.model._
import org.scalax.semweb.rdf.IRI
//import org.scalax.semweb.rdf.LongLiteral
import org.scalax.semweb.rdf.IntLiteral
import org.scalax.semweb.rdf.StringLiteral
import org.scalax.semweb.rdf.AnyLit
import org.scalax.semweb.rdf.DoubleLiteral
import org.scalax.semweb.rdf.BlankNode
import org.scalax.semweb.rdf.BooleanLiteral
import org.scalax.semweb.rdf.DecimalLiteral
import org.scalax.semweb.rdf.StringLangLiteral
import org.scalax.semweb.rdf.Quad
import org.openrdf.model.vocabulary.{XMLSchema=>xe}
import org.openrdf.model.vocabulary
/**
 * Implicit conversions from Scala-Semantic 2 Sesame
 */
trait Scala2SesameModelImplicits{

  implicit def IRI2URI(iri:IRI) = if(iri==null) null else new URIImpl(iri.stringValue)
  implicit def blankNode2BNode(b:BlankNode) = if(b==null) null else new BNodeImpl(b.stringValue)
  implicit def res2Resource(res:Res): Resource = res match {
    case null=>null
    case res:Resource=>res
    case iri:IRI=>this.IRI2URI(iri)
    case b:BlankNode=>this.blankNode2BNode(b)
  }

  implicit def rdfValue2Value(v:RDFValue): Value =v match {
    case null=>null
    case res:Resource=>res
    case iri:IRI=>this.IRI2URI(iri)
    case b:BlankNode=>this.blankNode2BNode(b)
    case l:Lit=>this.lit2Literal(l)
  }



  implicit def lit2Literal(literal:Lit):Literal = literal match {
    case null=>null
    case StringLangLiteral(text,lang)=>new LiteralImpl(text,lang)
    case lit:IntLiteral=>intLit2Literal(lit)
    case lit:DoubleLiteral=>doubleLit2Literal(lit)
    case lit:BooleanLiteral=>booleanLit2Literal(lit)
    case lit:DateLiteral=>dateLit2Literal(lit)
    case lit:DatatypeLiteral=> new LiteralImpl(lit.label, lit.dataType)
    case other=>new LiteralImpl(other.label)

  }


  implicit def langLi2Literal(lit:StringLangLiteral):Literal = new LiteralImpl(lit.text,lit.lang)
  implicit def intLit2Literal(lit:IntLiteral):Literal = new LiteralImpl(lit.value.toString,vocabulary.XMLSchema.INTEGER)

  implicit def doubleLit2Literal(lit:DoubleLiteral):Literal = new LiteralImpl(lit.value.toString,vocabulary.XMLSchema.DOUBLE)
  implicit def booleanLit2Literal(lit:BooleanLiteral):Literal = new LiteralImpl(lit.value.toString,vocabulary.XMLSchema.BOOLEAN)
  implicit def decimalLit2Literal(lit:DecimalLiteral):Literal = new LiteralImpl(lit.value.toString(),vocabulary.XMLSchema.DECIMAL)
  //implicit def longLit2Literal(lit:LongLiteral):Literal = new LiteralImpl(lit.value.toString,vocabulary.XMLSchema.LONG)
  implicit def dateLit2Literal(lit:DateLiteral):Literal = new LiteralImpl(lit.value.toString,vocabulary.XMLSchema.DATETIME)



  implicit class QuadStatement(q:Quad) extends Statement{
    lazy val  getSubject: Resource = q.sub

    lazy val getContext: Resource = q.cont

    lazy val  getPredicate: URI = q.pred

    lazy val getObject: Value = q.obj
  }

}

/**
 * Implicit conversions from Sesame to Scala-Semantic
 */
trait Sesame2ScalaModelImplicits{
  implicit def URI2IRI(uri:URI) = if(uri==null) null else IRI(uri.stringValue)
  implicit def Resource2Res(r:Resource)  = r match {
    case null=>null
    case b:BNode=>BlankNode(b.getID)
    case u:URI=>this.URI2IRI(u)
  }

  implicit def Value2RDFValue(value:Value):RDFValue = value match {
    case null=>null
    case uri:URI=>URI2IRI(uri)
    case b:BNode=>Resource2Res(b)
    case res:Resource=>Resource2Res(res)
    case l:Literal=>literal2Lit(l)
  }


  def isCalendar(l:Literal): Boolean =  l.getDatatype == xe.DATETIME ||
    l.getDatatype == xe.DATE ||
    l.getDatatype == xe.GMONTH ||
    l.getDatatype == xe.GMONTHDAY ||
    l.getDatatype == xe.GDAY ||
    l.getDatatype == xe.GYEAR ||
    l.getDatatype == xe.TIME ||
    l.getDatatype == xe.DAYTIMEDURATION ||
    l.getDatatype == xe.GYEARMONTH

  def isCalendar(uri:URI) =
    uri == xe.DATETIME ||
    uri == xe.DATE ||
    uri == xe.GMONTH ||
    uri == xe.GMONTHDAY ||
    uri == xe.GDAY ||
    uri == xe.GYEAR ||
    uri == xe.TIME ||
    uri == xe.DAYTIMEDURATION ||
    uri == xe.GYEARMONTH

  implicit def literal2Lit(l:Literal):Lit = l.getDatatype match {
    case null=>if(l==null) null else AnyLit(l.getLabel)
    case xe.BOOLEAN => BooleanLiteral(l.booleanValue())
    //case xe.DECIMAL => DecimalLiteral(l.decimalValue())
    case xe.DOUBLE | xe.DECIMAL => DoubleLiteral(l.doubleValue())
  //  case xe.LONG => LongLiteral(l.longValue())
    case xe.INT | xe.INTEGER | xe.POSITIVE_INTEGER | xe.NON_NEGATIVE_INTEGER => IntLiteral(l.intValue())
    case d if this.isCalendar(d) =>  DateLiteral(l.calendarValue().toGregorianCalendar.getTime)
    case xe.STRING | xe.NORMALIZEDSTRING=> if(l.getLanguage!="" && l.getLanguage!=null) StringLangLiteral(l.getLabel,l.getLanguage) else StringLiteral(l.getLabel)
    case other => AnyLit(l.getLabel)
  }


  implicit def Statement2Quad(st:Statement) =
    new Quad(
      Resource2Res(st.getSubject),
      URI2IRI(st.getPredicate),
      Value2RDFValue(st.getObject),
      Resource2Res(st.getContext))


  implicit def URI2PatEl(uri:URI):CanBePredicate = IRI(uri.stringValue)

  //implicit def URI2ResPatEl(uri:URI):ResourcePatEl = IRI(uri.stringValue)


  implicit def BNode2PatEl(bn:BNode):BNodePatEl = BlankNode(bn.stringValue)

  implicit def Value2PatEl(value:Value):CanBeObject = value match {
    case null=>null
    case lit:Literal=>literal2Lit(lit)
    case b:BNode=>this.BNode2PatEl(b)
    case iri:URI => URI2IRI(iri)
    case _ =>null
  }

  implicit def Resource2PatEl(r:Resource):CanBeSubject  = r match {
    case null=>null
    case b:BNode=>this.BNode2PatEl(b)
    case iri:URI => URI2IRI(iri)
    case _ =>null
  }

}