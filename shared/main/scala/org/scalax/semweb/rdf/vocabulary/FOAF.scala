package org.scalax.semweb.rdf.vocabulary

import org.scalax.semweb.rdf.IRI


/**
 * Foaf helper
 */
object FOAF {
  val namespace: String = "http://xmlns.com/foaf/0.1/"
  val foaf = IRI(namespace)
  val PERSON: IRI = foaf / "Person"
  val NAME: IRI = foaf / "name"
  val KNOWS: IRI = foaf / "knows"
  val MBOX: IRI = foaf / "mbox"


  //TODO: move to uppercase

  val Agent: IRI = foaf / "Agent"
  val Document: IRI = foaf / "Document"
  val Group: IRI = foaf / "Group"
  val Image: IRI = foaf / "Image"
  val LabelProperty: IRI = foaf / "LabelProperty"
  val OnlineAccount: IRI = foaf / "OnlineAccount"
  val OnlineChatAccount: IRI = foaf / "OnlineChatAccount"
  val OnlineEcommerceAccount: IRI = foaf / "OnlineEcommerceAccount"
  val OnlineGamingAccount: IRI = foaf / "OnlineGamingAccount"
  val Organization: IRI = foaf / "Organization"
  val PersonalProfileDocument: IRI = foaf / "PersonalProfileDocument"
  val Project: IRI = foaf / "Project"
  val account: IRI = foaf / "account"
  val accountName: IRI = foaf / "accountName"
  val accountServiceHomepage: IRI = foaf / "accountServiceHomepage"
  val age: IRI = foaf / "age"
  val aimChatID: IRI = foaf / "aimChatID"
  val based_near: IRI = foaf / "based_near"
  val birthday: IRI = foaf / "birthday"
  val currentProject: IRI = foaf / "currentProject"
  val depiction: IRI = foaf / "depiction"
  val depicts: IRI = foaf / "depicts"
  val familyName: IRI = foaf / "familyName"
  val firstName: IRI = foaf / "firstName"
  val gender: IRI = foaf / "gender"
  val givenName: IRI = foaf / "givenName"
  val homepage: IRI = foaf / "homepage"
  val icqChatID: IRI = foaf / "icqChatID"
  val img: IRI = foaf / "img"
  val interest: IRI = foaf / "interest"
  val isPrimaryTopicOf: IRI = foaf / "isPrimaryTopicOf"
  val jabberID: IRI = foaf / "jabberID"
  val lastName: IRI = foaf / "lastName"
  val logo: IRI = foaf / "logo"
  val made: IRI = foaf / "made"
  val maker: IRI = foaf / "maker"
  val mbox_sha1sum: IRI = foaf / "mbox_sha1sum"
  val member: IRI = foaf / "member"
  val membershipClass: IRI = foaf / "membershipClass"
  val msnChatID: IRI = foaf / "msnChatID"
  val myersBriggs: IRI = foaf / "myersBriggs"
  val nick: IRI = foaf / "nick"
  val openid: IRI = foaf / "openid"
  val page: IRI = foaf / "page"
  val pastProject: IRI = foaf / "pastProject"
  val phone: IRI = foaf / "phone"
  val plan: IRI = foaf / "plan"
  val primaryTopic: IRI = foaf / "primaryTopic"
  val publications: IRI = foaf / "publications"
  val schoolHomepage: IRI = foaf / "schoolHomepage"
  val sha1: IRI = foaf / "sha1"
  val skypeID: IRI = foaf / "skypeID"
  val status: IRI = foaf / "status"
  val thumbnail: IRI = foaf / "thumbnail"
  val tipjar: IRI = foaf / "tipjar"
  val title: IRI = foaf / "title"
  val topic: IRI = foaf / "topic"
  val topic_interest: IRI = foaf / "topic_interest"
  val weblog: IRI = foaf / "weblog"
  val workInfoHomepage: IRI = foaf / "workInfoHomepage"
  val workplaceHomepage: IRI = foaf / "workplaceHomepage"
  val yahooChatID: IRI = foaf / "yahooChatID"

}
