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

bintraySettings

Build.publishSettings
