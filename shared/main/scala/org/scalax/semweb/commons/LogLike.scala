package org.scalax.semweb.commons

/*
wrapper for logger (used to abstract from play logger as akka logger adapter is different
 */
trait LogLike extends scala.AnyRef {
  def isDebugEnabled : scala.Boolean
  def isInfoEnabled : scala.Boolean
  def isWarnEnabled : scala.Boolean
  def isErrorEnabled : scala.Boolean
  def debug(message : => scala.Predef.String) : scala.Unit
  def debug(message : => scala.Predef.String, error : => scala.Throwable) : scala.Unit
  def info(message : => scala.Predef.String) : scala.Unit
  def info(message : => scala.Predef.String, error : => scala.Throwable) : scala.Unit
  def warn(message : => scala.Predef.String) : scala.Unit
  def warn(message : => scala.Predef.String, error : => scala.Throwable) : scala.Unit
  def error(message : => scala.Predef.String) : scala.Unit
  def error(message : => scala.Predef.String, error : => scala.Throwable) : scala.Unit
}

/*
has logger
 */
trait Logged {

  def lg:LogLike
}
