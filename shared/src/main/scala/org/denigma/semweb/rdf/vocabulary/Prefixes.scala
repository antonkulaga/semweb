package org.denigma.semweb.rdf.vocabulary

object UI {
  def sp(str:String) = s"http://spinrdf.org/sp#$str"
  def spr(str:String) = s"http://spinrdf.org/spr#$str"
  def ui(str:String) = s"http://uispin.org/ui#$str"
}




class ILA
{

  val NAMESPACE = this ila ""
  def ila(str:String) = s"http://longevityalliance.org/$str"
  val RESOURCE: String = this ila "/resource"
  val PAGES = this ila "pages"

}

class Denigma
{
  val DE:String = "http://denigma.org/resource/"
  val OBO:String = "http://purl.obolibrary.org/obo/"
  def de(str:String) = s"http://denigma.org/resource/$str"

}