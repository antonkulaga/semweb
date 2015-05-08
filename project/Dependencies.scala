import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._


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
    "com.softwaremill.quicklens" %%% "quicklens" % Versions.quickLens
  ))


  lazy val semWebJVM:Def.Initialize[Seq[ModuleID]] =  Def.setting(base.value ++Seq(
    "com.github.benhutchison" %% "prickle" % Versions.prickle,
    "org.parboiled" %% "parboiled" % Versions.parboiled,
    "com.softwaremill.quicklens" %% "quicklens" % Versions.quickLens
  ))

  lazy val schemasJVM:Def.Initialize[Seq[ModuleID]] = Def.setting(base.value ++Seq(
    "org.openrdf.sesame" % "sesame-rio-turtle" %  Versions.bigDataSesame,
    "org.openrdf.sesame" % "sesame-sail-memory" % Versions.bigDataSesame,
    "org.w3" %% "banana-sesame" % Versions.banana,
    "com.pellucid" %% "framian" % Versions.framian
  ))

  lazy val schemasJS:Def.Initialize[Seq[ModuleID]] = Def.setting(base.value ++Seq(
    "org.w3" %%% "banana-sesame" % Versions.banana
  ))



  lazy val semWebSesame: Def.Initialize[Seq[ModuleID]] = Def.setting(base.value ++Seq(
    "org.openrdf.sesame" % "sesame-rio-turtle" %  Versions.bigDataSesame,
    "org.openrdf.sesame" % "sesame-sail-memory" % Versions.bigDataSesame,
    "com.bigdata" % "bigdata" % Versions.bigData % "test",
    "org.w3" %% "banana-sesame" % Versions.banana,
    "org.scalatest" %% "scalatest" % Versions.scalaTest % "test"
  ))



}
