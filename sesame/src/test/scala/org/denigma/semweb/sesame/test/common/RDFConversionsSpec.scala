package org.denigma.semweb.sesame.test.common

import org.openrdf.model
import org.scalatest._
import org.denigma.semweb.rdf._
import org.denigma.semweb.sesame._


/**
 * There will be tests for RDF conversion implicits
 */
class RDFConversionsSpec extends WordSpec{


  "Literals" should {

    "convert to sesame literals with language" in {
      val hello = StringLangLiteral("hello world","en")

      val sesameLiteral:model.Literal = hello
      sesameLiteral.stringValue()==hello.stringValue
      sesameLiteral.getLanguage == hello.lang

    }
  }

}
