package org.scalax.semweb.picklers

object SemanticRegistry extends SemanticRegistry{
  this.register()
}

class SemanticRegistry extends MapRegistry with MessagesPicklers{

  def register() = {
    this.registerCommon()
    this.registerMessages()
    this.registerRdf()
  }

}
