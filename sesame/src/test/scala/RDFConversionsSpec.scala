import org.scalatest._
import org.scalax.semweb.rdf
import org.scalax.semweb.rdf._
import org.scalax.semweb.sesame._
import org.openrdf.model
import org.openrdf.model.vocabulary


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
