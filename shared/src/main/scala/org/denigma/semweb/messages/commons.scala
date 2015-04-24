package org.denigma.semweb.messages

import java.util.Date

trait StorageMessage extends Channeled{
  def id:String
  def time:Date
}

object Channeled {
  val default = "default"

}

/**
 * Each source has its id, it is "channel"
 */
trait Channeled{
  def channel:String
}
