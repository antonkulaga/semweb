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

 val semWebVer = "0.6.19"

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
    version := semWebVer,
    resolvers += "bintray-alexander_myltsev" at "http://dl.bintray.com/alexander-myltsev/maven/",
    resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
    parallelExecution in Test := false
  )

  val sharedSettings: Seq[Setting[_]] = sameSettings++Seq(
      name := "semweb",
      version := semWebVer,
      libraryDependencies += "org.parboiled" %%% "parboiled" % "2.0.1",
      libraryDependencies += "com.lihaoyi" %% "utest" % "0.2.4",
      testFrameworks += new TestFramework("utest.runner.JvmFramework")

  )

  val jsSettings: Seq[Setting[_]] = Seq(

    libraryDependencies +=  "com.lihaoyi" %% "utest" % "0.2.4" % "test",
    libraryDependencies += "org.scalajs" %%% "scalajs-pickling" % "0.3.1",
    libraryDependencies += "com.github.benhutchison" %%% "prickle" % "1.1.2"
  )

  val jvmSettings : Seq[Setting[_]] = Seq(
    libraryDependencies += "org.scalajs" %% "scalajs-pickling-play-json" % "0.3.1",
    libraryDependencies += "com.github.benhutchison" %% "prickle" % "1.1.2"
  )


  implicit val js: JsTarget = new JsTarget(settings = JsTarget.js.settings ++ jsSettings)
  implicit val jvm: JvmTarget = new JvmTarget(settings = JvmTarget.jvm.settings ++ jvmSettings)

  val semwebModule = XModule(id = "semweb",  defaultSettings = publishSettings ++ sharedSettings ++ XScalaSettings )

  lazy val semweb            = semwebModule.project(semwebJvm, semwebJs)
  lazy val semwebJvm         = semwebModule.jvmProject(semwebSharedJvm)
  lazy val semwebJs          = semwebModule.jsProject(semwebSharedJs)
  lazy val semwebSharedJvm   = semwebModule.jvmShared()
  lazy val semwebSharedJs    = semwebModule.jsShared(semwebSharedJvm)

  val bigDataSesameVersion = "2.7.13" //BigData devs are lazy to update, but I have to use their sesame in order no to brake my apps that depend on sesame


  lazy val sesame = Project(
    id = "sesame",

    base = file("sesame"),

    settings = sameSettings ++ publishSettings ++ Seq(

    name := "semweb-sesame",

    parallelExecution in Test := false,

    resolvers += "Bigdata releases" at "http://systap.com/maven/releases/",

    resolvers += "nxparser-repo" at "http://nxparser.googlecode.com/svn/repository/",

    resolvers += "apache-repo-releases" at "http://repository.apache.org/content/repositories/releases/",

    libraryDependencies += "org.openrdf.sesame" % "sesame-rio-turtle" %  bigDataSesameVersion,

    libraryDependencies += "org.openrdf.sesame" % "sesame-sail-memory" % bigDataSesameVersion,

    libraryDependencies += "com.bigdata" % "bigdata" % "1.4.0" % "test",

    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

    //libraryDependencies += "org.w3" %% "sesame" % "0.7.1"

  )).dependsOn(semwebJvm)


}
