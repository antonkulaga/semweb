package org.denigma.schemas.web

import org.denigma.semweb.rdf.vocabulary
import org.denigma.semweb.rdf.vocabulary.WI.PLATFORM
import org.denigma.semweb.rdf.vocabulary._
import org.denigma.semweb.shex._
trait  ChatSchema extends UserSchema {

  val message = WI.pl("Message")
  val comment = WI.pl("Comment")

  val hasReply= WI.pl("has_reply")
  val isReplyTo= WI.pl("is_reply_to")

  val (title,text,creator,date) = (DCTerms.title,DCTerms.text,DCElements.creator,vocabulary.DCElements.date)

  //val commentShapeRes = WI.pl("CommentShape")
  val messageShapeRes = WI.pl("MessageShape")

  val messageShape = new ShapeBuilder(messageShapeRes) has
    title of XSD.StringDatatypeIRI occurs Opt isCalled "Title" hasPriority 0 and
    text of XSD.StringDatatypeIRI occurs ExactlyOne isCalled "Text" hasPriority 1  and
    creator ofShape userShortShape isCalled "Author" occurs Plus  hasPriority 2 and
    date of XSD.DateTime isCalled "Published" occurs ExactlyOne hasPriority 3 and
    hasReply ofShape messageShapeRes isCalled "Has comments" occurs Star hasPriority 4 and
    isReplyTo ofShape messageShapeRes isCalled "Is reply to" occurs Star hasPriority 5 and
    RDF.TYPE of message occurs Plus isCalled "class" hasPriority -1 default message shape


  //val postShape = ShapeBuilder(WI.pl("PostShape")) basedOn messageShape has



}
