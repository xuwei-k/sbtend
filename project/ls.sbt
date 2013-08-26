resolvers ++= Seq(
  Resolver.url("sbt plugin releases", url(
    "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(
      Resolver.ivyStylePatterns),
  "less is" at "http://repo.lessis.me",
  "coda" at "http://repo.codahale.com"
)

libraryDependencies <+= (sbtVersion in update,scalaBinaryVersion){
  (sbtV, scalaV) =>
  val (sbtBinaryV,lsV) = sbtV.split('.') match{
    case Array(_,"12",_ @ _*) => ("0.12", "0.1.3")
    case Array(_,"13",_ @ _*) => ("0.13", "0.1.3")
  }
  Defaults.sbtPluginExtra("me.lessis" % "ls-sbt" % lsV,sbtBinaryV,scalaV)
}

