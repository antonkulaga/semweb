import sbt._
import sbt.Keys._
import bintray.Plugin.bintraySettings
import bintray.Keys._

organization := "org.scalax"

name := "semweb-sesame"

scalaVersion := "2.10.4"

version := Build.semWebVer

libraryDependencies += "org.openrdf.sesame" % "sesame-model" % "2.7.10"

libraryDependencies += "com.lihaoyi" % "utest_2.10" % "0.1.2-JS" % "test"

autoCompilerPlugins := true

bintraySettings

Build.publishSettings
