import sbt._
import sbt.Keys._
import bintray.Plugin.bintraySettings
import bintray.Keys._

organization := "org.scalax"

name := "semweb-sesame"

scalaVersion := "2.10.4"

version := Build.semWebVer

resolvers += "Bigdata releases" at "http://systap.com/maven/releases/"

resolvers += "nxparser-repo" at "http://nxparser.googlecode.com/svn/repository/"

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "apache-repo-releases" at "http://repository.apache.org/content/repositories/releases/"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies +="com.bigdata" % "bigdata" % "1.3.0"

libraryDependencies += "org.openrdf.sesame" % "sesame-model" % "2.7.11"

libraryDependencies += "com.lihaoyi" % "utest_2.10" % "0.1.2-JS" % "test"

autoCompilerPlugins := true

bintraySettings

Build.publishSettings
