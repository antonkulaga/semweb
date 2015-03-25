import sbt.Keys._
import sbt._
import bintray.Opts
import bintray.Plugin.bintraySettings
import bintray.Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject


object Build extends sbt.Build
{

 publishMavenStyle := false

 val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

 protected val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug



  lazy val publishSettings: Seq[Setting[_]] =  bintraySettings ++ Seq(
    repository in bintray :=  "denigma-releases",

    bintrayOrganization in bintray := Some("denigma"),

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
    organization := "org.denigma",
    scalaVersion :="2.11.6",
    version := Versions.semWeb,
    resolvers += "bintray-alexander_myltsev" at "http://dl.bintray.com/alexander-myltsev/maven/",
    resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
    parallelExecution in Test := false,
    initialCommands in console := """
                                    |import org.scalax.semweb.rdf._
                                    |import org.scalax.semweb.sparql._
                                    |import org.scalax.semweb.composites
                                    |""".stripMargin
  )

  val sharedSettings: Seq[Setting[_]] = sameSettings++Seq(
      name := "semweb",
      version := Versions.semWeb,
      libraryDependencies ++= Dependencies.shared.value
  )

  val jsSettings: Seq[Setting[_]] = publishSettings++ Seq(
    name := "semweb",
    libraryDependencies ++=  Dependencies.semWebJS.value,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    jsDependencies += RuntimeDOM % "test"
  )

  val jvmSettings : Seq[Setting[_]] =  publishSettings++Seq(
    name := "semweb",
    libraryDependencies ++=  Dependencies.semWebJVM.value,
    testFrameworks += new TestFramework("utest.runner.Framework")
  )

  //val semwebModule = XModule(id = "semweb",  defaultSettings = publishSettings ++ sharedSettings ++ XScalaSettings )
  lazy val semweb = CrossProject("semweb",new File("."),CrossType.Full).
    settings(sharedSettings: _*).
    jsSettings(jsSettings: _* ).
    jvmSettings( jvmSettings: _* )

  lazy val semwebJs = semweb.js
  lazy val semwebJvm   = semweb.jvm



/*  lazy val semweb            = semwebModule.project(semwebJvm, semwebJs)
  lazy val semwebJvm         = semwebModule.jvmProject(semwebSharedJvm)
  lazy val semwebJs          = semwebModule.jsProject(semwebSharedJs)
  lazy val semwebSharedJvm   = semwebModule.jvmShared()
  lazy val semwebSharedJs    = semwebModule.jsShared(semwebSharedJvm)*/

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
