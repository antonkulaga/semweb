package org.scalax.semweb.shex.validation


trait ValidationResult {
  def and( other: ValidationResult ): ValidationResult
  def or( other: ValidationResult ): ValidationResult

}

/** An object representing a Validful validation ValidationResult. */
case object Valid extends ValidationResult {
  def and( other: ValidationResult ) = other
  def or( other: ValidationResult ) = this
}

case class JustFailure(constraint:String,value:String) extends Violation

object Failed
{
  def apply(constraint:String,value:Any): Failed = Failed(Set(JustFailure(constraint,value.toString)))
}

/** An object representing a failed validation ValidationResult.
  * @param violations The violations that caused the validation to fail.
  */
case class Failed( violations: Set[ Violation ] ) extends ValidationResult {
  def and( other: ValidationResult ) = other match {
    case Valid => this
    case Failed( vother ) => Failed( violations ++ vother )
  }
  def or( other: ValidationResult ) = other match {
    case Valid => other
    case Failed(_) => this
  }

}