import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
object Versions {

  val semWeb = "0.7.1"

  val bigData =  "1.5.1"

  val bigDataSesame = "2.7.13" //BigData devs are lazy to update, but I have to use their sesame in order no to brake my apps that depend on sesame

  val banana = "0.8.1"

  val prickle = "1.1.5"
  
  val parboiled =  "2.1.0"
  
  val utest = "0.3.1" // is totally broken for me for JS subprojects
  
  val scalaTest = "2.2.4"
  
  val framian = "0.3.3"

  val quickLens = "1.3.1"

}

object Dependencies
{

  val base= Def.setting(Seq())
  
  lazy val shared = Def.setting(base.value++Seq(
     "com.lihaoyi" %%% "utest" % Versions.utest
  ))

  lazy val semWebJS:Def.Initialize[Seq[ModuleID]] =  Def.setting(base.value ++Seq(
    "com.lihaoyi" %% "utest" % Versions.utest % "test",
   "com.github.benhutchison" %%% "prickle" % Versions.prickle,
    "com.github.japgolly.fork.parboiled" %%% "parboiled" % Versions.parboiled,
    "com.softwaremill.quicklens" %%% "quicklens" % "1.3.1"
  ))


  lazy val semWebJVM:Def.Initialize[Seq[ModuleID]] =  Def.setting(base.value ++Seq(
    "com.github.benhutchison" %% "prickle" % Versions.prickle,
    "org.parboiled" %% "parboiled" % Versions.parboiled,
    "com.softwaremill.quicklens" %% "quicklens" % "1.3.1"
  ))

  lazy val semWebSesame: Def.Initialize[Seq[ModuleID]] = Def.setting(base.value ++Seq(
    "org.openrdf.sesame" % "sesame-rio-turtle" %  Versions.bigDataSesame,
    "org.openrdf.sesame" % "sesame-sail-memory" % Versions.bigDataSesame,
    "com.bigdata" % "bigdata" % Versions.bigData % "test",
    "org.w3" %% "banana-sesame" % Versions.banana,
    "org.scalatest" %% "scalatest" % Versions.scalaTest % "test",
    "com.pellucid" %% "framian" % Versions.framian % "test"
  ))



}
