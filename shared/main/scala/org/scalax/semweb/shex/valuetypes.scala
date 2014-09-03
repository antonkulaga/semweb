package org.scalax.semweb.shex

import org.scalax.semweb.rdf._



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

  val property: IRI =  rs / "allowedValue"
}

case class ValueSet(s: Set[RDFValue]) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {

    val prop =  Quads -- subject -- ValueSet.property
    s.map(v=>prop -- v -- context).toSet[Quad]
  }

  override def toTriplets(subject: Res): Set[Trip] = s.map{s=>
    Trip(subject,ValueSet.property,s)
  }

}
object ValueAny

case class ValueAny(stem: ValueStem) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = ???

  override def toTriplets(subject: Res): Set[Trip] = ???


}

object ValueStem {
  val property = se / "stem"
}

case class ValueStem(s: IRI) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
    Set(Quad(subject,ValueStem.property, s, context))
  }

  override def toTriplets(subject: Res): Set[Trip] =Set( Trip(subject, ValueStem.property,s) )

}
object ValueReference {
  val property = se / "valueShape"
}

case class ValueReference(l: Label) extends ValueClass {
  override def toQuads(subject: Res)(implicit context: Res): Set[Quad] = {
    Set(Quad(subject,ValueReference.property, l.asResource, context))
  }

  override def toTriplets(subject: Res): Set[Trip] = {
    Set(Trip(subject,ValueReference.property, l.asResource))
  }

}
