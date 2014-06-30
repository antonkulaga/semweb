package org.scalax.semweb.picklers

object SemanticRegistry extends SemanticRegistry

class SemanticRegistry extends MapRegistry with MessagesPicklers{

  def register() = {
    this.registerCommon()
    this.registerRdf()
  }

}
