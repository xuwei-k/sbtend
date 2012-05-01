libraryDependencies <+= (sbtVersion) { sv =>
  if(sv.split('.')(1).toInt <= 11)
    "org.scala-tools.sbt" %% "scripted-plugin" % sv
  else
    "org.scala-sbt" % "scripted-plugin" % sv
}
