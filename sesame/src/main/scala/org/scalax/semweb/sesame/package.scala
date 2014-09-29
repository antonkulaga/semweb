package org.scalax.semweb

import org.openrdf.model.vocabulary.{XMLSchema=>xe}

/**
 * Sesame extensions
 */
package object sesame extends ResultsImplicits with Scala2SesameModelImplicits with Sesame2ScalaModelImplicits  with ConnectionImplicits