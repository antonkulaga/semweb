package org.denigma.schemas.web

import org.denigma.schemas.common.BasicSchema
import org.denigma.semweb.rdf.vocabulary
import org.denigma.semweb.rdf.vocabulary.{WI, XSD}
import org.denigma.semweb.shex._


trait Blog extends BasicSchema
{

  protected val username = WI.pl("username")
  protected val userpic = WI.pl("user")
  val userShortShape  = ShapeBuilder(WI.pl("User_Short_Shape")) has
    username isCalled "username" and
    userpic isCalled "userpic" shape


  val postShape = new ShapeBuilder(WI.pl("Post_Shape")) has
    vocabulary.DCTerms.title of XSD.StringDatatypeIRI occurs Opt isCalled "Title" and
    vocabulary.DCTerms.text of XSD.StringDatatypeIRI occurs Star and
    vocabulary.DCElements.creator ofShape userShortShape isCalled "Author" occurs Plus and
    vocabulary.DCElements.date of XSD.DateTime shape


  
}
/*

trait Page extends BasicSchema {
  protected val page = WI.PLATFORM.
}*/
