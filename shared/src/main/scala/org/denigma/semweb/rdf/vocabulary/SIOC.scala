package org.denigma.semweb.rdf.vocabulary

import org.denigma.semweb.rdf.IRI

object SIOC {

  val namespace: String = "http://rdfs.org/sioc/ns#"
  val sioc = IRI(namespace)

  val Community = sioc / "Community"
  val Container = sioc / "Container"
  val Forum = sioc / "Forum"
  val Item = sioc / "Item"
  val Post = sioc / "Post"
  val Role = sioc / "Role"
  val Space = sioc / "Space"
  val Site = sioc / "Site"
  val Thread = sioc / "Thread"
  val Account = sioc / "Account"
  val Usergroup = sioc / "Usergroup"

  val account_of = sioc / "account_of"
  val addressed_to = sioc / "addressed_to"
  val administrator_of = sioc / "administrator_of"
  val attachment = sioc / "attachment"
  val avatar = sioc / "avatar"
  val container_of = sioc / "container_of"
  val content = sioc / "content"
  val creator_of = sioc / "creator_of"
  val earlier_version = sioc / "earlier_version"
  val email = sioc / "email"
  val email_sha1 = sioc / "email_sha1"
  val embeds_knowledge = sioc / "embeds_knowledge"
  val feed= sioc / "feed"
  val follows= sioc / "follows"
  val function_of = sioc / "function_of"
  val has_administrator = sioc / "has_administrator"
  val has_container = sioc / "has_container"
  val has_discussion = sioc / "has_discussion"
  val has_function = sioc / "has_function"
  val has_host = sioc / "has_host"
  val has_member = sioc / "has_member"
  val has_moderator = sioc / "has_moderator"
  val has_modifier_of = sioc / "has_modifier_of"
  val has_owner = sioc / "has_owner"
  val has_parent = sioc / "has_parent"
  val has_reply= sioc / "has_reply"
  val has_scope = sioc / "has_scope"
  val has_space = sioc / "has_space"
  val has_subscriber = sioc / "has_subscriber"
  val has_usergroup = sioc / "has_usergroup"
  val host_of = sioc / "host_of"
  val id = sioc / "id"
  val ip_address = sioc / "ip_address"
  val last_activity_date = sioc / "last_activity_date"
  val last_item_date= sioc / "last_item_date"
  val last_reply_date = sioc / "last_reply_date"
  val later_version = sioc / "later_version"
  val latest_version = sioc / "latest_version"
  val link = sioc / "link"
  val links_to = sioc / "links_to"
  val member_of = sioc / "member_of"
  val moderator_of = sioc / "moderator_of"
  val name = sioc / "name"
  val next_by_date = sioc / "next_by_date"
  val next_version = sioc / "next_version"
  val note = sioc / "note"
  val num_authors = sioc / "num_authors"
  val num_items = sioc / "num_items"
  val num_replies = sioc / "num_replies"
  val num_threads = sioc / "num_threads"
  val num_views = sioc / "num_views"
  val owner_of = sioc / "owner_of"
  val parent_of = sioc / "parent_of"
  val previous_by_date = sioc / "previous_by_date"
  val previous_version = sioc / "previous_version"
  val related_to = sioc / "related_tor"
  val reply_of = sioc / "reply_of"
  val scope_of = sioc / "scope_of"
  val sibling = sioc / "sibling"
  val space_of = sioc / "space_of"
  val subscriber_of = sioc / "subscriber_of"
  val topic = sioc / "topic"
  val usergroup_of = sioc / "usergroup_of"



}
