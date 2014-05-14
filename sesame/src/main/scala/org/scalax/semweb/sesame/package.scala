package org.scalax.semweb

import org.openrdf.model._
import org.openrdf.model.impl._
import org.openrdf.model.impl.{BNodeImpl, LiteralImpl, URIImpl}
import org.openrdf.model.vocabulary.{XMLSchema=>xe}
import org.openrdf.model.Literal

import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.IRI
import org.openrdf.query.{BindingSet, TupleQueryResult}
import scala.collection.immutable._
import scala.collection.JavaConversions._
import org.openrdf.repository.RepositoryResult
import org.openrdf.model.vocabulary
import org.scalax.semweb.sesame._

/**
 * Sesame extensions
 */
package object sesame extends ResultsImplicits with Scala2SesameModelImplicits  with ConnectionImplicits