package org.scalax.semweb.rdf


/**
 * Quad builder
 */
object Quads{


  class WithSubject(val sub:Res) extends RDFBuilder[WithProperty]{

    def --(prop:IRI):WithProperty = this -- new WithProperty(sub,prop)
    def triplets: Set[Trip] = this.values.flatMap(_.triplets)


    def quads: Set[Quad] = this.values.flatMap(_.quads)
    //def quads(context:Res): Set[Quad] = this.values.flatMap(_.quads(context))

  }

  class WithProperty(sub:Res,prop:IRI) extends RDFBuilder[WithObject]{
    def --(obj:RDFValue):WithObject = this -- new WithObject(Trip(sub,prop,obj))

    def triplets:Set[Trip] = this.values.map(_.triplet)
    def quads: Set[Quad] = this.values.flatMap(_.values)
    //def quads(context:Res): Set[Quad] = this.values.flatMap(_.quads(context))


  }

  class WithObject(val triplet:Trip) extends RDFBuilder[Quad]{

    def --(res:Res):Quad = this -- Quad(triplet.sub,triplet.pred,triplet.obj,res)

    def quads = this.values

    //TODO;test that it works in a right way
    //def quads(context:Res): Set[Quad] = if(values.isEmpty) Set(Quad(triplet.sub,triplet.pred,triplet.obj,context)) else quads
  }




  def model(res:Res):WithSubject  = new WithSubject(res)

  /**
   * For nice syntax like Quads -- subject -- property -- object -- context
   * @param res
   * @return
   */
  def --(res:Res) = model(res)

}
abstract class RDFBuilder[T]{
  var values = Set.empty[T]

  def --(value:T): T = {
    this.values = values+value
    value
  }
}

/** '
  * QUad
  * @param sub subject
  * @param pred predicate
  * @param obj object
  * @param cont context
  */
case class Quad(sub:Res,pred:IRI,obj:RDFValue,cont:Res = null) extends /*DiHyperEdge[RDFValue]((s,p,o,c)) with*/ BasicTriplet{
  override def hasContext: Boolean = cont!=null

  override def stringValue: String = s"\n ${sub.toString} ${pred.toString} ${obj.toString}" + (if(hasContext) " "+cont.toString+" .\n" else " .\n")

  override def equals(that: Any): Boolean = that match  {

    case Quad(s,p,o,c) => sub==s && pred == p && obj ==o && cont == c
    case Trip(s,p,o) if !this.hasContext =>sub==s && pred == p && obj ==o

    case _=>false
  }

}


case class Trip(sub:Res,pred:IRI,obj:RDFValue) extends /*DiHyperEdge[RDFValue]((s,p,o)) with*/ BasicTriplet

trait BasicTriplet extends RDFElement
{
  def hasContext: Boolean = false

  val sub:Res
  val pred:IRI
  val obj:RDFValue

  def toSet: Set[RDFValue] = Set(sub,pred,obj)
  def toList: List[RDFValue] = List(sub,pred,obj)
  def allResources: Set[Res] = this.toSet.collect{case r:Res=>r}
  def allIRIs: Set[IRI] = this.toSet.collect{case iri:IRI=>iri}


  def objectString: String = obj   match {
//    case lit:LitStr=> "\""+lit.stringValue()+"\""
//    case lit:LitString=> "\""+lit.stringValue()+"\""
//    case lit:Literal=> "\""+lit.stringValue()+"\""
    case uri:IRI => s"${uri.toString}"
    //case l:Literal if l.getDatatype==null=>"\""+l.stringValue()+"\""
    case other=>other.toString
  }

  //override def stringValue: String = s"\n <${sub.stringValue}> <${pred.stringValue}> $objectString .\n"

  override def stringValue: String = s"\n ${sub.toString} ${pred.toString} $objectString .\n"


  override def equals(that: Any): Boolean = that match  {

    case Trip(s,p,o) if !this.hasContext =>sub==s && pred == p && obj ==o
    case q @ Quad(s,p,o,c) if !q.hasContext=> sub==s && pred == p && obj ==o

    case _=>false
  }
}
