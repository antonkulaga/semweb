package org.scalax.semweb.rdf

case class Quad(s:Res,p:IRI,o:RDFValue,c:Res = null) extends /*DiHyperEdge[RDFValue]((s,p,o,c)) with*/ BasicTriplet{
  override def hasContext: Boolean = c!=null

  override def stringValue: String = s"\n ${s.toString} ${p.toString} ${o.toString}" + (if(hasContext) " "+c.toString+" .\n" else " .\n")

}


case class Trip(s:Res,p:IRI,o:RDFValue) extends /*DiHyperEdge[RDFValue]((s,p,o)) with*/ BasicTriplet

trait BasicTriplet extends QueryElement
{
  def hasContext: Boolean = false

  val s:Res
  val p:IRI
  val o:RDFValue


  def objectString: String = o   match {
//    case lit:LitStr=> "\""+lit.stringValue()+"\""
//    case lit:LitString=> "\""+lit.stringValue()+"\""
//    case lit:Literal=> "\""+lit.stringValue()+"\""
    case uri:IRI => s"<${uri.stringValue}>"
    //case l:Literal if l.getDatatype==null=>"\""+l.stringValue()+"\""
    case other=>other.stringValue
  }

  override def stringValue: String = s"\n <${s.stringValue}> <${p.stringValue}> $objectString .\n"


}
