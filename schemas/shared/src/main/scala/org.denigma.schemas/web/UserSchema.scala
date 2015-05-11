package org.denigma.schemas.web

import org.denigma.schemas.common.BasicSchema
import org.denigma.semweb.rdf.StringLiteral
import org.denigma.semweb.rdf.vocabulary
import org.denigma.semweb.rdf.vocabulary._
import org.denigma.semweb.shex.{Opt, Plus, ShapeBuilder}

trait UserSchema extends BasicSchema
{
  val user = USERS.UserClass

  val account = vocabulary.FOAF.account
  val nick = vocabulary.FOAF.nick

  val ushort = WI.pl("UserShortShape")
  val ushape = WI.pl("UserShape")

  val userShortShape  = ShapeBuilder(ushort) has
    FOAF.NAME isCalled "username" hasPriority 0 and
    SIOC.avatar isCalled "avatar" occurs Opt hasPriority 1 and
    RDF.TYPE of user isCalled "class" hasPriority -1 default user shape

  val userShape = ShapeBuilder(ushape) basedOn userShortShape has
    FOAF.firstName of XSD.StringDatatypeIRI isCalled "Name" occurs Plus  hasPriority 2 and
    FOAF.lastName of XSD.StringDatatypeIRI isCalled "Last name" occurs Plus  hasPriority 3 and
    FOAF.MBOX isCalled "email"  hasPriority 4 shape //TODO add some other info

}
