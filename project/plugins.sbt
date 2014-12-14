resolvers +=  Resolver.url("scala-js-releases", url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("bintray-sbt-plugin-releases",url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.5.0")

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.2")

addSbtPlugin("com.lihaoyi" % "utest-js-plugin" % "0.2.4")

// Wrapper plugin for scalajs
addSbtPlugin("com.github.inthenow" % "sbt-scalajs" % "0.56.7")