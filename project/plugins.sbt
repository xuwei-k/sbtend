libraryDependencies <+= (sbtVersion) { sv =>
  if(sv.split('.')(1).toInt <= 11)
    "org.scala-tools.sbt" %% "scripted-plugin" % sv
  else
    "org.scala-sbt" % "scripted-plugin" % sv
}

resolvers ++= Seq(
  "less is" at "http://repo.lessis.me",
  "coda" at "http://repo.codahale.com")

addSbtPlugin("me.lessis" % "ls-sbt" % "0.1.1")
