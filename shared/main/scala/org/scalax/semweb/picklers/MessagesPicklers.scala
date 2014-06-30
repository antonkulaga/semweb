package org.scalax.semweb.picklers

import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.PicklerRegistry._
import org.scalax.semweb.messages.Results.SelectResults
import org.scalax.semweb.messages.StringQueryMessages

trait MessagesPicklers extends RDFPicklers
{
  self:PicklerRegistry=>

  def registerMessages() = {
    register[StringQueryMessages.Ask]
    register[StringQueryMessages.Construct]
    register[StringQueryMessages.Select]
    register[StringQueryMessages.Update]


    register[SelectResults]
  }

}
