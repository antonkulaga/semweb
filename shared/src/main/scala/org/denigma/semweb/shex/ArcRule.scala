package org.denigma.semweb.shex
import org.denigma.semweb.rdf._
import org.denigma.semweb.rdf.vocabulary.{RDFS, WI, RDF}
import org.denigma.semweb.sparql.{GP, Variable}


object ArcRule {

  val clazz = se / "ArcRule"

  val property = rs / "property"

  val priority: IRI = WI.pl("priority")

  val default: IRI = WI.pl("default")

  def apply(propertyName:IRI,id:Res): ArcRule = apply(propertyName,RDF.VALUE,Star,None,id)

  def apply(propertyName:IRI,tp:IRI,id:Res): ArcRule =   apply(propertyName,tp,Star,None,id)

  def apply(propertyName:IRI,tp:IRI,card:Cardinality,id:Res): ArcRule = apply(propertyName,tp,card,None,id)

  def apply(propertyName:IRI,tp:IRI,card:Cardinality, priority:Option[Int],id:Res): ArcRule =
  {
    ArcRule(id,NameTerm(propertyName),ValueType(tp),card, priority = priority)
  }

}



case class ArcRule(
                    id: Label,
                    name: NameClass,
                    value: ValueClass,
                    occurs: Cardinality,
                    actions: Seq[Action] = List.empty,
                    priority:Option[Int] = None, //the smaller the more important
                    title:Option[String] = None,
                    default:Option[RDFValue] = None
                    ) extends Rule
{

  lazy val me = id.asResource

  override def toQuads(subj: Res)(implicit context: Res): Set[Quad] = {
    val aboutMe = Set(
      Quad(subj, ArcRule.property, me, context),
      Quad(me, RDF.TYPE, ArcRule.clazz, context)
    )
    val tlt: Set[Quad] =  this.title.fold(Set.empty[Quad])(t=>Set(Quad(me, vocabulary.DCElements.title,t,context)))
    val prior: Set[Quad] = this.priority.fold(Set.empty[Quad])(p=>Set(Quad(me, ArcRule.priority,IntLiteral(p),context)))
    aboutMe ++  tlt ++ prior ++   name.toQuads(me)(context) ++ value.toQuads(me)(context) ++  occurs.toQuads(me)(context)
  }

  override def toTriplets(subj:Res):Set[Trip] = {
    val aboutMe = Set(
      Trip(subj, ArcRule.property, me),
      Trip(me, RDF.TYPE, ArcRule.clazz)
    )
    val tlt: Set[Trip] =  this.title.fold(Set.empty[Trip])(t=>Set(Trip(me, vocabulary.DCElements.title,t)))
    val prior: Set[Trip] = this.priority.fold(Set.empty[Trip])(p=>Set(Trip(me, ArcRule.priority,IntLiteral(p))))
    aboutMe++ tlt ++ prior   ++  name.toTriplets(me) ++ value.toTriplets(me) ++  occurs.toTriplets(me)
  }

}