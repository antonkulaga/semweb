semweb
======

SemWeb (Semantic Web) is a generic Scala/ScalaJS semantic web library that can be used both client and serverside.


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


