package org.denigma.semweb.rdf.vocabulary

/**
 * Prefixes for organizations
 */
class ORGS extends PrefixConfig(WI.namespace / "orgs"){

  val org = this /+ "organization"

  val lab = this /+ "lab"

  val state = this /+ "state"

  val group = this /+ "group"



}
