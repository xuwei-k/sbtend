sbtPlugin := true

name := "sbtend"

organization := "com.github.xuwei_k"

description := "sbt xtend plugin"

version := "0.1.2-SNAPSHOT"

homepage := Some(url("https://github.com/xuwei-k/sbtend"))

licenses := Seq("MIT License" -> url("https://github.com/xuwei-k/sbtend/blob/master/LICENSE.txt"))

scalacOptions := Seq("-deprecation", "-unchecked")

ScriptedPlugin.scriptedSettings

ScriptedPlugin.scriptedBufferLog := false

watchSources <++= (sbtTestDirectory).map{ dir => (dir ***).get }

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

libraryDependencies ++= Seq(
   "log4j" % "log4j" % "1.2.16" % "compile",
   "org.eclipse.xtend2" % "xtend-maven-plugin" % "2.2.2"
)

publishTo := sys.env.get("MAVEN_DIRECTORY").map{ dir =>
  Resolver.file("gh-pages",file(dir))(Patterns(true, Resolver.mavenStyleBasePattern))
}

lsSettings

LsKeys.tags in LsKeys.lsync := Seq("sbt", "xtend")

(externalResolvers in LsKeys.lsync) := Seq(
  "xuwei-k maven repo" at "http://xuwei-k.github.com/mvn/"
)
