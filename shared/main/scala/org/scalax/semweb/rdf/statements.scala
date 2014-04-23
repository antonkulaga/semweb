package org.scalax.semweb.rdf

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

trait BasicTriplet extends QueryElement
{
  def hasContext: Boolean = false

  val sub:Res
  val pred:IRI
  val obj:RDFValue


  def objectString: String = obj   match {
//    case lit:LitStr=> "\""+lit.stringValue()+"\""
//    case lit:LitString=> "\""+lit.stringValue()+"\""
//    case lit:Literal=> "\""+lit.stringValue()+"\""
    case uri:IRI => s"<${uri.stringValue}>"
    //case l:Literal if l.getDatatype==null=>"\""+l.stringValue()+"\""
    case other=>other.stringValue
  }

  override def stringValue: String = s"\n <${sub.stringValue}> <${pred.stringValue}> $objectString .\n"

  override def equals(that: Any): Boolean = that match  {

    case Trip(s,p,o) if !this.hasContext =>sub==s && pred == p && obj ==o
    case q @ Quad(s,p,o,c) if !q.hasContext=> sub==s && pred == p && obj ==o

    case _=>false
  }
}
