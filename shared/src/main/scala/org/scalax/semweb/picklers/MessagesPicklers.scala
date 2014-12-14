package org.scalax.semweb.picklers

import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.PicklerRegistry._
import org.scalax.semweb.messages.Results.SelectResults
import org.scalax.semweb.messages.{Results, Read, StringQueryMessages}

trait MessagesPicklers extends RDFPicklers
{
  self:PicklerRegistry=>

  def registerMessages() = {
    register[StringQueryMessages.Ask]
    register[StringQueryMessages.Construct]
    register[StringQueryMessages.Select]
    register[StringQueryMessages.Update]

    register[Read.Bind]
    register[Read.Construct]
    register[Read.Query]
    register[Read.Question]
    register[Read.Search]
    register[Read.Select]



    register[Results.SelectResults]
  }

}
