import sbt._
import sbt.Keys._
import bintray.Plugin.bintraySettings
import bintray.Keys._

organization := "org.scalax"

name := "semweb-sesame"

scalaVersion := "2.10.4"

version := Build.semWebVer

val currentSesameVersion = "2.7.11"

val bigDataSesameVersion = "2.6.10" //BigData devs are lazy to update, but I have to use their sesame in order no to brake my apps that depend on sesame

resolvers += "Bigdata releases" at "http://systap.com/maven/releases/"

resolvers += "nxparser-repo" at "http://nxparser.googlecode.com/svn/repository/"

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "apache-repo-releases" at "http://repository.apache.org/content/repositories/releases/"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies += "org.openrdf.sesame" % "sesame-sail-memory" % bigDataSesameVersion

libraryDependencies += "com.bigdata" % "bigdata" % "1.3.0" % "test" //Bigdata for tests

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.5"

parallelExecution in Test := false

//libraryDependencies += "com.lihaoyi" %% "utest" % "0.1.2" % "test"

autoCompilerPlugins := true

bintraySettings

Build.publishSettings
