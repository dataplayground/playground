// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.4")

// web plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")
addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "1.0.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")

// docker packaging
addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.2.0")

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"

// code style
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.5.1")

// dont env loading in DEV
resolvers += Resolver.url("bintray-sbt-plugin-releases", url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.1")

resolvers += Classpaths.sbtPluginReleases
addSbtPlugin("au.com.onegeek" %% "sbt-dotenv" % "1.1.33")
