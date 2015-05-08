import sbt.Keys._
import sbt._
import bintray._
import BintrayPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject


object Build extends sbt.Build
{

 publishMavenStyle := false

 val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

 protected val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug



  lazy val publishSettings: Seq[Setting[_]] =   Seq(
    bintrayRepository  :=  "denigma-releases",

    bintrayOrganization := Some("denigma"),

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
    resolvers += Resolver.sonatypeRepo("releases"),
    scalaVersion := Versions.scala,
    version := Versions.semWeb,
    parallelExecution in Test := false,
    initialCommands in console := """
                                    |import org.denigma.semweb.rdf._
                                    |import org.denigma.semweb.sparql._
                                    |import org.denigma.semweb.composites
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
    enablePlugins(BintrayPlugin).
    jsSettings(jsSettings: _* ).
    jvmSettings( jvmSettings: _* )

  lazy val semwebJs = semweb.js
  lazy val semwebJvm   = semweb.jvm

  lazy val schemas = CrossProject("schemas",new File("schemas"),CrossType.Full)
    .settings(sameSettings: _*)
    .settings(
      name := "schemas",
      version := "0.1",
      scalaVersion := Versions.scala
    )
    .dependsOn(semweb)
    .enablePlugins(BintrayPlugin)
    .jvmSettings(
      libraryDependencies ++= Dependencies.schemasJVM.value
      // Add JVM-specific settings here
    )
    .jsSettings(
      // Add JS-specific settings here
    )

  lazy val schemasJVM = schemas.jvm //I know it is bad to do this way
  lazy val schemasJS = schemas.js


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

    updateOptions := updateOptions.value.withCachedResolution(true),

    initialCommands in console := """
                                    |import org.denigma.semweb.rdf._
                                    |import org.denigma.semweb.sparql._
                                    |import org.denigma.semweb.sesame._
                                    |""".stripMargin

  )).enablePlugins(BintrayPlugin).dependsOn(semwebJvm).dependsOn(schemasJVM  % "test")


}
