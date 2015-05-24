package org.denigma.semweb.rdf.vocabulary

import org.denigma.semweb.rdf.IRI


/**
 * My own internal set of prefixes. In future I should separate it from semweb
 */
object WI extends PrefixConfig("http://webintelligence.eu/"){


  val RESOURCE: String = this / "resource"

  val PAGES = this /+ "pages"

  val CONFIG = this /+ "conf"
  /**
   * Useful properties for Web projects
   */
  object PLATFORM extends PrefixConfig(WI.namespace / "platform/"){

    val SHAPE_FOR = IRI(this / "shapeFor")
    val HAS_SHAPE = IRI(this / "hasShape")
    val ALL_SHAPES = IRI(this / "AllShapes")
    val EMPTY = IRI(this / "Empty")
    val DRAFT_OF = IRI(this / "draft_of")
    val BASE = IRI(this / "has_base")
    val allShapes = IRI(this / "AllShapes")

    val hasMenu = IRI(this / "has_menu" )
    val hasItem = IRI(this / "has_item" )
    val hasTitle = IRI(this / "has_title" )
    val hasHeader = (this / "has_header").iri
    val hasText = (this / "has_text").iri
    val hasName = (this / "has_name").iri


    val priority: IRI = IRI(this / "priority")

    val default: IRI = IRI(this / "default")


  }

  var IRIs: Set[IRI] = Set.empty[IRI]

  val root: IRI= this set "root"

  val context: IRI = this set "context"

  def re(str:String): IRI = IRI(RESOURCE / str)

  def pl(str:String): IRI = IRI(PLATFORM / str)

  def pg(page:String): IRI = IRI(PAGES / page)

  def conf(name:String): IRI = IRI(CONFIG / name)

  def set(name:String): IRI= conf(name)

  def set(res:IRI): IRI = { IRIs=IRIs+res; res}




}



/**
 * A class to construct prefixes, TODO: deprecate it
 */
class PrefixConfig(val namespace:String)
{
  def / (name:String): String = namespace / name
  def /+(name:String) = new PrefixConfig(namespace / name)

  def stringValue = namespace
}
