import bintray.Opts
import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._
import scala.scalajs.sbtplugin
import bintray.Plugin.bintraySettings
import bintray.Keys._

scalaJSSettings

Build.sharedSettings

unmanagedSourceDirectories in Compile <+= baseDirectory(_ / ".." / "shared" / "main")

unmanagedSourceDirectories in Test <+= baseDirectory(_ / ".." / "shared" / "test")

autoCompilerPlugins := true

libraryDependencies += "org.scalajs" %%% "scalajs-pickling" % "0.3.1"

resolvers += "bintray-alexander_myltsev" at "http://dl.bintray.com/alexander-myltsev/maven/"

libraryDependencies += "org.parboiled" %%% "parboiled" % "2.0.1"

bintraySettings

Build.publishSettings
