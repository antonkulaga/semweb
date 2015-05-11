package org.denigma.semweb.sesame.test.data

import java.util.Date

import org.denigma.schemas.web.ChatSchema
import org.denigma.semweb.rdf.vocabulary.{FOAF, SIOC, WI, RDF}
import org.denigma.semweb.rdf._
import org.denigma.semweb.shex.ShapeBuilder
import org.openrdf.model.{Value, URI, Resource}

import scala.collection.immutable.List

trait BlogTestData extends JustTestData with  ChatSchema
{

  /*  val messageShape = new ShapeBuilder(messageShapeRes) has
    title of XSD.StringDatatypeIRI occurs Opt isCalled "Title" hasPriority 0 and
    text of XSD.StringDatatypeIRI occurs ExactlyOne isCalled "Text" hasPriority 1  and
    creator ofShape userShortShape isCalled "Author" occurs Plus  hasPriority 2 and
    date of XSD.DateTime isCalled "Published" occurs ExactlyOne hasPriority 3 and
    hasReply ofShape messageShapeRes isCalled "Has comments" occurs Star hasPriority 4 and
    isReplyTo ofShape messageShapeRes isCalled "Is reply to" occurs Star hasPriority 5 and
    RDF.TYPE of message occurs Plus isCalled "class" hasPriority -1 default message shape*/

  /*
   val userShortShape  = ShapeBuilder(ushort) has
      FOAF.NAME isCalled "username" hasPriority 0 and
      SIOC.avatar isCalled "avatar" occurs Opt hasPriority 1 and
      RDF.TYPE of user isCalled "class" hasPriority -1 default user shape
  */
  val rybka = IRI("http://rybka.org.ua")
  val news = rybka / "news"

  val aqua = news / "aquarium"
  val delivery = news / "delivery"
  val question = news / "question"
  val answer = news / "answer"

  val users = rybka / "users"
  val nowicki  = users / "Nowicki"
  val somebody = users / "Somybody"
  val rep1 = news / "reply1"
  val rep2 = news / "reply2"

  val aquaPost = Quads -- aqua
  aquaPost -- title -- StringLiteral("Aquarium started")
  aquaPost -- text -- StringLiteral("We started aquarium!")
  aquaPost -- creator -- nowicki
  aquaPost -- date -- DateLiteral(new Date())
  aquaPost -- RDF.TYPE -- message
  val aquaTrips = aquaPost.triplets

  val nowickiProfile = Quads -- nowicki
  nowickiProfile -- FOAF.NAME -- StringLiteral("Nowicki")
  nowickiProfile -- SIOC.avatar -- rybka / "nowicki.pic"
  nowickiProfile -- RDF.TYPE -- user
  val nowickiTrips = nowickiProfile.triplets


  val somebodyProfile = Quads -- somebody
  somebodyProfile -- FOAF.NAME -- StringLiteral("Somebody")
  somebodyProfile -- SIOC.avatar -- rybka / "somybody.pic"
  somebodyProfile -- RDF.TYPE -- user
  val somebodyTrips = somebodyProfile.triplets


  val deliveryPost = Quads -- delivery
  deliveryPost -- title -- StringLiteral("Aquarium started")
  deliveryPost -- text -- StringLiteral("We started aquarium!")
  deliveryPost -- creator -- nowicki
  deliveryPost -- date -- DateLiteral(new Date())
  deliveryPost -- RDF.TYPE -- message
  deliveryPost -- hasReply -- rep1
  val deliveryTrips = deliveryPost.triplets


    val reply1 = Quads -- rep1
    reply1  -- text -- StringLiteral("Really?")
    reply1  -- creator --  somebody
    reply1  -- date -- DateLiteral(new Date())
    reply1  -- RDF.TYPE -- message
    reply1 -- isReplyTo -- delivery
    val reply1Trips = reply1.triplets

    val reply2 = Quads -- rep2
    reply2  -- text -- StringLiteral("Yup!")
    reply2  -- creator -- nowicki
    reply2  -- date -- DateLiteral(new Date())
    reply2  -- RDF.TYPE -- message
    reply2 -- isReplyTo -- rep1
    val reply2Trips = reply2.triplets




  import org.denigma.semweb.sesame._

  import org.denigma.semweb.shex._

  lazy val allShapeData = userShortShape.asQuads(IRI(WI.CONFIG.namespace)).map(t=>t:Trip) ++
    userShape.asQuads(IRI(WI.CONFIG.namespace)).map(t=>t:Trip) ++
    messageShape.asQuads(IRI(WI.CONFIG.namespace)).map(t=>t:Trip)

  lazy val allData = allShapeData ++ aquaTrips  ++ nowickiTrips ++ somebodyTrips ++ reply1Trips ++ reply2Trips ++ deliveryTrips

  lazy val testData = allData.map{
    case trip=>(trip.sub:Resource,trip.pred:URI,trip.obj:Value)
  }.toList
}
