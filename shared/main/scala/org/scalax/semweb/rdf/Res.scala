package org.scalax.semweb.rdf

/**
 * Implementation of resource
 */
trait Res extends RDFValue with CanBeSubject
trait CanBeSubject extends CanBeObject
