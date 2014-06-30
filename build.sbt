import sbt._
import sbt.Keys._
import bintray.Plugin.bintraySettings
import bintray.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._

lazy val root = project.in(file("."))//.settings(crossScalaVersions := Seq("2.10.4", "2.11.0"))

lazy val js = project.in(file("js"))

lazy val sesame = project.in(file("sesame")).dependsOn(root)

Build.sharedSettings

version := Build.currentVersion

unmanagedSourceDirectories in Compile <+= baseDirectory(_ / "shared" / "main" / "scala")

unmanagedSourceDirectories in Test <+= baseDirectory(_ / "shared" / "test" / "scala")

test in Test <<= (test in Test) dependsOn (test in (sesame, Test)) //run sesame tests when rdfs are tested


libraryDependencies ++= Seq(
  "com.lihaoyi" %% "utest" % "0.1.7" % "test"
)

testFrameworks += new TestFramework("utest.runner.JvmFramework")

addCompilerPlugin("com.lihaoyi" %% "acyclic" % "0.1.2")


libraryDependencies += "org.scalajs" %% "scalajs-pickling-play-json" % "0.3.1"

autoCompilerPlugins := true

bintraySettings

Build.publishSettings
