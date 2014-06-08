import sbt._
import sbt.Keys._
import bintray.Plugin.bintraySettings
import bintray.Keys._
import Def.ScopedKey
import sbt.URLRepository
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import scala.Some

// import com.typesafe.sbt.packager.universal.UniversalKeys
// import play.Keys._

object Build extends sbt.Build{

 val isRelease = true //as snapshots are still not supported by bintray-sbt, I mark lib as release

 def repo = if(isRelease) "scalax-releases" else "scalax-snapshots"

 val semWebVer = "0.4.6"

 publishMavenStyle := false

 val currentVersion = semWebVer//if(this.isRelease) semWebVer else semWebVer+"-SNAPSHOT"

 val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

 protected val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug

  lazy val publishSettings = Seq(
    repository in bintray := this.repo,

    bintrayOrganization in bintray := Some("scalax"),

    licenses += ("MPL-2.0", url("http://opensource.org/licenses/MPL-2.0")),

    bintrayPublishIvyStyle := true
  )



  /**
   * For parts of the project that we will not publish
   */
  lazy val noPublishSettings = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )

  val sharedSettings = Seq(
      organization := "org.scalax",
      //scalaVersion :="2.11.1",
      scalaVersion :="2.10.4",
      name := "semweb",
      version := Build.semWebVer

  )

  val scalajsResolver: URLRepository = Resolver.url("scala-js-releases",
    url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
      Resolver.ivyStylePatterns)

}
