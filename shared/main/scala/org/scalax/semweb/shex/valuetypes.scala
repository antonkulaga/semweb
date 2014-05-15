package org.scalax.semweb.shex

import org.scalax.semweb.rdf.vocabulary._

import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.Quad
import org.scalax.semweb.sparql.{Variable, Pat}
import scala.util.{Success, Failure, Try}



sealed trait ValueClass extends ToQuads with ToTriplets
{

}

object ValueType {
  val property = rs / "valueType"
}

case class ValueType(v: Res) extends ValueClass{
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
    Set(Quad(subject,rs / "valueType", v, context))
  }

  override def toTriplets(subject: Res): Set[Trip] = Set(Trip(subject,ValueType.property, v))



//  override def toPatterns(res: Res): (Set[Pat], Map[String, Variable]) = t match {
//
//    case x if x.stringValue.contains(vocabulary.XSD.namespace) =>
//      println("datatypes are not supported yet")
//      this.empty
//
//    case y=>Set(Pat(p,RDF.TYPE,v))
//
//  }


//  override def toPatterns(res: Res, vars: Map[String, Variable]): Try[Set[Pat]] = vars.get("property") match {
//    case Some(p)=> if(v.stringValue.contains(XSD.namespace)) {
//      print("DATA TYPES ARE NOT IMPLEMENTED")
//      Success(Set.empty)
//    }
//    else
//    {
//      print("WARNING SUBCLASSES ARE NOT IMPLEMENTED")
//      Success(Set(Pat(p,RDF.TYPE,v)))
//    }
//    case None =>Failure(new IllegalArgumentException(s"no property variable for ValueType ${v.stringValue}"))
//  }
}

object ValueSet {

  val property =  rs / "allowedValue"
}

case class ValueSet(s: Set[Res]) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {

    val prop =  Quads -- subject -- ValueSet.property
    s.map(v=>prop -- v -- context).toSet[Quad]
  }

  override def toTriplets(subject: Res): Set[Trip] = s.map{s=>
    Trip(subject,ValueSet.property,s)
  }

}
case class ValueAny(stem: IRIStem) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???

  override def toTriplets(subject: Res): Set[Trip] = ???


}
case class ValueStem(s: IRI) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
    Set(Quad(subject,se / "stem", s, context)) //TODO change
  }

  override def toTriplets(subject: Res): Set[Trip] = ???

}
case class ValueReference(l: Label) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
    Set(Quad(subject,se / "valueShape", l.asResource, context))
  }

  override def toTriplets(subject: Res): Set[Trip] = ???


}
