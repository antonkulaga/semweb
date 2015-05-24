semweb
======

SemWeb (Semantic Web) is a generic Scala/ScalaJS semantic web library that can be used both on client and server sides.
The lib is at rather early stage but is already used in some webapps. Only a part of what is planned has been implemented,
in general you should use the lib when:
* you want to have same rdf-related classes and use them both on frontend/backend. In such case prickle scalajs does serialization job and, implicit sesame/jena conversions
will allow you to send quries in easy format  [works]
* are tired of writing string for SPARQL queries and want a DSL that will let you create quries in convenient type-safe manner  [partially done]
* want to validate the data entered by users at client or server. For this purpose ShEx is used.  [in development]
* want to generate SPARQL updates from annotated scala classes [not yet started]

Setting up:
-----------

1 Add denigma resolvers:
```scala
    resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases")
```

3) Add version that you want to use (see published versions at bintray https://bintray.com/denigma )
If you use release version it looks like:

```scala

    libraryDependencies += "org.denigma" %% "semweb" % "0.7.4"// for scala projects OR

    libraryDependencies += "org.denigma" %%% "semweb" % "0.7.4"// for scalajs projects, note %%% is used
```

4). Use it in your project!



Contribution:
-------------

Ideas and pull-requests are always welcome:
```
    git clone https://github.com/denigma/semweb
    sbt test //to run tests
```

NOTES: As lib is cross Scala/ScalaJS most of the code including tests is in shared folder. For cross scala/sclaajs testing u-test lib is used,
for scala-only tests ScalaTest lib is used.

WARNING: test coverage is not full, most of the test runs for both scala and scalajs

semweb subprojects
==================

semweb subproject
==================

Project that contains core classes. Most of the code is crosscompiled between Scala/ScalaJS. Some important packages:

org.denigma.semweb.rdf
---------------------

IRI, Resource, BlankNode, Literals, Quads/Triplets as well as some other basic RDF classes + vocabulary.
What is the most important is that they can be easily pickled/unpickled to be used both on backend and frontend.
RDF classes also inherit from PatterElement and other QueryElement traits so you can easily use them in SPARQL DSL without conversions


org.denigma.semweb.sparql
------------------------

SPARQL DSL that allows making typesafe SPARQL quries. Thy syntax is like:

```scala

      val selMenu:SelectQuery = SELECT (?("item"),?("title")) WHERE (
        Pat( IRI("http://domain.com/foo"), IRI("http://domain.com/hasMenu"), ?("menu") ),
        Pat( ?("menu"), IRI("http://domain.com/hasItem"), ?("item")),
        Pat( item, hasTitle, ?("title"))
      )

```
Where ?("variable_name") is for SPARQL variables, and Pat is for SPARQL graph patterns.
At the moment SPARQL DSL is on rather early stage (only the most widely used operators are supported). Big part of the code maybe rewritten in future

org.denigma.semweb.shex
----------------------

RDF Shape Expressions ( see specification here http://www.w3.org/2001/sw/wiki/ShEx and http://www.w3.org/2013/ShEx/Primer.html ) were one of the main reasons why
this library was created they allow to validate user input and can be used for generation of user interfaces.

Most of the code is inspired by https://github.com/labra/ShExcala

org.denigma.semweb.composites
-----------------------------

Contains picklers for semweb classes. Prickle library is used for (de)serialization https://github.com/benhutchison/prickle 


sesame subproject
=================

Subproject that provides integration with Sesame-based databases (see http://rdf4j.org to learn about Sesame). Important subprojects are:

org.denigma.semweb.sesame
-------------------------

Implicit conversions from semweb.rdf classes to sesame rdf model and vice versa.
As well as some implicit classes to make working with sesame more pleasant. There is also an idea to make similar package for Jena

WARNING: it uses outdated version of sesame by default because BigData developers did not find time to upgrate to sesame 2.7.11

org.denigma.semweb.sesame.io
----------------------------

Some IO operations. WARNING: it is using banana-rdf ( https://gitter.im/w3c/banana-rdf )
In a long distant future there is a goal to gradually port semweb code to banana-rdf 


org.denigma.semweb.sesame.shex
------------------------------

Contains the code that generates SPARQL CONSTRUCT QUERIES that extract shapes and also data

schemas subproject
==================

Contains some test data and some useful data-shapes. Usually is used for testing.