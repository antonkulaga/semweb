import org.scalax.semweb.rdf.IRI
import utest._
import org.scalax.semweb.rdf
object RDFSuite extends TestSuite{

  def tests = TestSuite {
    "scalax_rdf_test" - {
      "trailing_slash_test" - {

        IRI("http://foo.com")
        val bar1 = IRI("http://foo.com") / "bar"
        val bar2 = IRI("http://foo.com/") / "bar"
        assert(bar1.stringValue == bar2.stringValue)
        assert(bar1 == bar2)

      }



    }
  }

}