package org.denigma.semweb.shex.validation



/** A base trait for all violation types. */
trait Violation {
  /** The actual runtime value of the object under validation. */
  def value: Any

  /** A textual description of the constraint being violated (for example, "must not be empty"). */
  def constraint: String
}