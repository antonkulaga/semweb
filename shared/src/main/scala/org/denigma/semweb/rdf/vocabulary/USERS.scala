package org.denigma.semweb.rdf.vocabulary

import org.denigma.semweb.rdf.IRI
import org.denigma.semweb.shex.ShapeBuilder


object USERS extends PrefixConfig(WI.namespace / "users")
{
  self=>




  val user: PrefixConfig = this /+ "user"
  val hasPasswordHash = WI.pl("hasPasswordHash")// (WI.PROPERTIES / "hasPasswordHash").iri
  val hasEmail:IRI = FOAF.MBOX

  val UserClass: IRI = WI.pl("User")//WI.CLASSES / "User" iri

}
