libraryDependencies <+= (sbtVersion){ sv =>
  sv.split('.') match{
    case Array(_,a,b,_ @ _*) =>
      val i = a.toInt
      if((i <= 10) || (i <= 11) && (b.toInt <= 2))
        "org.scala-tools.sbt" %% "scripted-plugin" % sv
      else if(i == 11)
        "org.scala-sbt" %% "scripted-plugin" % sv
      else
        "org.scala-sbt" % "scripted-plugin" % sv
  }
}

