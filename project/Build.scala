import com.inthenow.sbt.scalajs.XModule
import sbt.Keys._
import sbt._
import com.inthenow.sbt.scalajs._
import com.inthenow.sbt.scalajs.SbtScalajs._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import bintray.Opts
import bintray.Plugin.bintraySettings
import bintray.Keys._


object Build extends sbt.Build
{

 publishMavenStyle := false

 val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

 protected val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug



  lazy val publishSettings: Seq[Setting[_]] =  bintraySettings ++ Seq(
    repository in bintray :=  "scalax-releases",

    bintrayOrganization in bintray := Some("scalax"),

    licenses += ("MPL-2.0", url("http://opensource.org/licenses/MPL-2.0")),

    bintrayPublishIvyStyle := true
  )



  /**
   * For parts of the project that we will not publish
   */
  lazy val noPublishSettings: Seq[Setting[_]] = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )

  val sameSettings:Seq[Setting[_]] = Seq(
    organization := "org.scalax",
    scalaVersion :="2.11.5",
    version := Versions.semWeb,
    resolvers += "bintray-alexander_myltsev" at "http://dl.bintray.com/alexander-myltsev/maven/",
    resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
    parallelExecution in Test := false,
    initialCommands in console := """
                                    |import org.scalax.semweb.rdf._
                                    |import org.scalax.semweb.sparql._
                                    |""".stripMargin
  )

  val sharedSettings: Seq[Setting[_]] = sameSettings++Seq(
      name := "semweb",
      version := Versions.semWeb,
      libraryDependencies ++= Dependencies.shared.value,
      testFrameworks += new TestFramework("utest.runner.JvmFramework")

  )

  val jsSettings: Seq[Setting[_]] = Seq(  libraryDependencies ++=  Dependencies.semWebJS.value  )

  val jvmSettings : Seq[Setting[_]] = Seq(  libraryDependencies ++=  Dependencies.semWebJVM.value  )


  implicit val js: JsTarget = new JsTarget(settings = JsTarget.js.settings ++ jsSettings)
  implicit val jvm: JvmTarget = new JvmTarget(settings = JvmTarget.jvm.settings ++ jvmSettings)

  val semwebModule = XModule(id = "semweb",  defaultSettings = publishSettings ++ sharedSettings ++ XScalaSettings )

  lazy val semweb            = semwebModule.project(semwebJvm, semwebJs)
  lazy val semwebJvm         = semwebModule.jvmProject(semwebSharedJvm)
  lazy val semwebJs          = semwebModule.jsProject(semwebSharedJs)
  lazy val semwebSharedJvm   = semwebModule.jvmShared()
  lazy val semwebSharedJs    = semwebModule.jsShared(semwebSharedJvm)

  lazy val sesame = Project(
    id = "sesame",

    base = file("sesame"),

    settings = sameSettings ++ publishSettings ++ Seq(

    name := "semweb-sesame",

    parallelExecution in Test := false,

    resolvers += "Bigdata releases" at "http://systap.com/maven/releases/",

    resolvers += "nxparser-repo" at "http://nxparser.googlecode.com/svn/repository/",

    resolvers += "apache-repo-releases" at "http://repository.apache.org/content/repositories/releases/",
    
    libraryDependencies ++= Dependencies.semWebSesame.value,

    initialCommands in console := """
                                    |import org.scalax.semweb.rdf._
                                    |import org.scalax.semweb.sparql._
                                    |import org.scalax.semweb.sesame._
                                    |""".stripMargin

  )).dependsOn(semwebJvm)


}
