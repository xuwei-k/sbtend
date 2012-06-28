sbtPlugin := true

name := "sbtend"

organization := "com.github.xuwei_k"

description := "sbt xtend plugin"

version := "0.1.3-SNAPSHOT"

homepage := Some(url("https://github.com/xuwei-k/sbtend"))

licenses := Seq("MIT License" -> url("https://github.com/xuwei-k/sbtend/blob/master/LICENSE.txt"))

scalacOptions := Seq("-deprecation", "-unchecked")

initialCommands in console := Seq(
  "sbtend","org.eclipse.xtext.xtend2.compiler.batch"
).map{"import " + _ + "._"}.mkString("\n")

externalResolvers ~= { _.filterNot{_.name.contains("Scala-Tools")} }

resolvers ++= Seq(
  "http://fornax-platform.org/nexus/content/groups/public/",
  "https://oss.sonatype.org/content/repositories/releases/",
  "http://build.eclipse.org/common/xtend/maven/",
  "http://maven.eclipse.org/nexus/content/groups/public/"
).map{u => u at u}


libraryDependencies ++= {
val xtendVersion = "2.3.0"
Seq(
   "log4j" % "log4j" % "1.2.16" % "compile",
   "org.eclipse.xtend" % "org.eclipse.xtend.lib" % xtendVersion,
   "org.eclipse.xtext" % "org.eclipse.xtext.xbase.lib" % xtendVersion,
   "org.eclipse.xtend" % "org.eclipse.xtend.standalone" % xtendVersion,
   "org.eclipse.emf" % "codegen" % "2.2.3"
)
}

publishTo := sys.env.get("MAVEN_DIRECTORY").map{ dir =>
  Resolver.file("gh-pages",file(dir))(Patterns(true, Resolver.mavenStyleBasePattern))
}

lsSettings

LsKeys.tags in LsKeys.lsync := Seq("sbt", "xtend")

(externalResolvers in LsKeys.lsync) := Seq(
  "xuwei-k maven repo" at "http://xuwei-k.github.com/mvn/"
)
