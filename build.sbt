name := """playground"""
organization := "geoHeil"

maintainer in Docker := "Georg Heiler"
dockerExposedPorts in Docker := Seq(9000, 4040)

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"
// TODO update to latest version
val sparkVersion = "1.5.1"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.apache.spark"  %% "spark-core"              % sparkVersion,
  "org.apache.spark"  %% "spark-streaming"         % sparkVersion,
  "org.apache.spark"  %% "spark-sql"               % sparkVersion,
  "org.apache.spark"  %% "spark-streaming-kafka"   % sparkVersion,
  "org.twitter4j"     % "twitter4j-core"           % "4.0.3",
  "org.twitter4j"     % "twitter4j-stream"         % "4.0.3",

//  "com.softwaremill.reactivekafka" %% "reactive-kafka-core" % "0.8.2",
//  "com.typesafe.akka" % "akka-stream-experimental_2.11"    % "2.0-M1",
//  "com.typesafe.akka" % "akka-http-core-experimental_2.11" % "2.0-M1",

  "org.webjars" % "bootstrap" % "3.3.5",
  "org.webjars" % "angularjs" % "1.4.7",
  "org.webjars" % "angular-ui-bootstrap" % "0.14.3",
  "org.webjars.bower" % "angular-websocket" % "1.0.14",
  specs2 % Test
)
  .map(_.exclude("org.slf4j", "slf4j-log4j12" ))

pipelineStages := Seq(uglify, digest, gzip)
pipelineStages in Assets := Seq()
pipelineStages := Seq(uglify, digest, gzip)
DigestKeys.algorithms += "sha1"
UglifyKeys.uglifyOps := { js =>
  Seq((js.sortBy(_._2), "concat.min.js"))
}

scalariformSettings

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
