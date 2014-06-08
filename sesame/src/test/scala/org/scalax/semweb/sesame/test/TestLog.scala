package org.scalax.semweb.sesame.test

import org.scalax.semweb.commons.LogLike

object TestLog extends LogLike{
  override def isDebugEnabled: Boolean = true

  override def warn(message: => String): Unit = print(s"WARNING: $message")

  override def warn(message: => String, error: => Throwable): Unit = print(s"WARNING: $message with ERROR ${error.getMessage}")

  override def isErrorEnabled: Boolean = true

  override def isInfoEnabled: Boolean = true

  override def error(message: => String): Unit = print(s"ERROR: $message")

  override def error(message: => String, error: => Throwable): Unit =  print(s"ERROR: $message with ERROR ${error.getMessage}")

  override def debug(message: => String): Unit =  print(s"DEBUG: $message")

  override def debug(message: => String, error: => Throwable): Unit =  print(s"DEBUG: $message with ERROR ${error.getMessage}")

  override def isWarnEnabled: Boolean = true

  override def info(message: => String): Unit = print(s"INFO: $message")

  override def info(message: => String, error: => Throwable): Unit =  print(s"INFO: $message with ERROR ${error.getMessage}")
}
