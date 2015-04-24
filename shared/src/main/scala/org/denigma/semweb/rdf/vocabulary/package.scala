package org.denigma.semweb.rdf


/**
 * implicit class that adds some imlicits
 */
package object vocabulary {


  implicit class StringPath(str:String) {

    def /(child:String): String = if(str.endsWith("/") || str.endsWith("#")) str+child else str+ "/" +child

    def /(child:IRI): IRI =  IRI(str / child.toString)

    def iri: IRI = IRI(str)

  }
}
