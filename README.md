semweb
======

SemWeb (Semantic Web) is a generic Scala/ScalaJS semantic web library that can be used both on client and server sides.

Usage:
------

1 Add sbt-bintray plugin (for more info see https://github.com/softprops/bintray-sbt ) to your plugins.sbt:

```scala

    resolvers += Resolver.url("bintray-sbt-plugin-releases",url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

    addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.1")
```

2 Add scalax resolvers:
```scala
resolvers += bintray.Opts.resolver.repo("scalax", "scalax-snapshots") //for snapshots
```
or
```scala
resolvers += bintray.Opts.resolver.repo("scalax", "scalax-releases") //for releases
```

3) Add version that you want to use (see published versions at bintray https://bintray.com/scalax )
If you use snapshot version it looks like:

```scala

    libraryDependencies += "org.scalax" %% "semweb" % "0.2"// for scala projects

    libraryDependencies += "org.scalax" %% "semweb" % "0.2-JS"// for scalajs projects
```
If you use release version it looks like:

```scala

    libraryDependencies += "org.scalax" %% "semweb" % "0.1"// for scala projects

    libraryDependencies += "org.scalax" %% "semweb" % "0.1-JS"// for scalajs projects
```

4). Use it in your project!

NOTES:

* The lib does not crosscompile to scala 2.11 yet but I am working on that.
* Due to the bug in bintray sbt plugin ( https://github.com/softprops/bintray-sbt/issues/17 ) I cannot add "SNAPSHOT" to the version
that is why I have versions withouth "SNAPSHOT" sufix in scalax-snapshots repo.

Contribution:
-------------

Ideas and pull-requests are always welcome:
```
    git clone https://github.com/scalax/semweb
    sbt test //to run tests
```

NOTES: As lib is cross Scala/ScalaJS most of the code including tests is in shared folder. For cross scala/sclaajs testing u-test lib is used,
for scala-only tests ScalaTest lib is used.
WARNING: the lib is not well covered with tests, so if you want to change something seriously, ask first


semweb packages:
================


org.scalax.semweb.rdf
---------------------

IRI, Resource, BlankNode vocabulary as well as some other basic RDF classes.
What is the most important is that they can be easily pickled/unpickled to be used both on backend and frontend.


org.scalax.semweb.sparql
------------------------

SPARQL DSL that allows making typesafe SPARQL quries. Thy syntax is like:

```scala

      val selMenu:SelectQuery = SELECT (?("item"),?("title")) WHERE {
        Pat( IRI("http://domain.com/foo"), IRI("http://domain.com/hasMenu"), ?("menu") )
        Pat( ?("menu"), IRI("http://domain.com/hasItem"), ?("item"))
        Pat( item, hasTitle, ?("title"))
      }

```
Where ?("variable_name") is for SPARQL variables, and Pat is for SPARQL graph patterns.
At the moment SPARQL DSL is on rather early stage (only the most widely used operators are supported)

org.scalax.semweb.shex
----------------------

RDF Shape Expressions ( see specification here http://www.w3.org/2001/sw/wiki/ShEx and http://www.w3.org/2013/ShEx/Primer.html ) were one of the main reasons why
this library was created they allow to validate user input and can be used for generation of user interfaces.

Only several classes are there at the moment, most of the code is yet to be transferred from https://github.com/labra/ShExcala


org.scalax.semweb.sesame
-------------------------

Implicit conversions from semweb.rdf classes to sesame rdf model and vice versa.
As well as some implicit classes to make working with sesame more pleasant. There is also an idea to make similar package for Jena


