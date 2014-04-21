package org.scalax.semweb.rdf.vocabulary

import org.scalax.semweb.rdf.IRI

/*
refactor
 */
object WI extends PrefixConfig("http://webintelligence.eu/"){


  val RESOURCE: String = this / "resource"

  val PAGES = this /+ "pages"

  val PLATFORM = this /+ "platform"

  val POLICY: PrefixConfig = this /+"policy"

  val SETTINGS: PrefixConfig =  this /+ "settings"

  val CLASSES: PrefixConfig = this /+ "classes"

  val PROPERTIES: PrefixConfig = this /+ "properties"

  def re(str:String): IRI = IRI(RESOURCE / str)

  def pg(page:String): IRI = IRI(PAGES / page)

  val CONFIG = this /+ "conf"

  def conf(name:String): IRI = IRI(CONFIG / name)

  def po(str:String): IRI = IRI(POLICY /str)

  def set(res:IRI): IRI = { IRIs=IRIs+res; res}

  def set(name:String): IRI= this set IRI(SETTINGS / name)

  var IRIs: Set[IRI] = Set.empty[IRI]

  val root: IRI= this set "root"

  val context: IRI = this set "context"
}



/**
 * Main namespace
 */
class PrefixConfig(val namespace:String)
{
  def / (name:String): String = namespace / name
  def /+(name:String) = new PrefixConfig(namespace / name)

  def stringValue = namespace
}
