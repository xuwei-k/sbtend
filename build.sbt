sbtPlugin := true

name := "sbtend"

organization := "com.github.xuwei_k"

description := "sbt xtend plugin"

version := "0.1.1-SNAPSHOT"

homepage := Some(url("https://github.com/xuwei-k/sbtend"))

scalacOptions := Seq("-deprecation", "-unchecked")

ScriptedPlugin.scriptedSettings

ScriptedPlugin.scriptedBufferLog := false

watchSources <++= (sbtTestDirectory).map{ dir => (dir ***).get }

initialCommands in console := Seq(
  "com.github.xuwei_k"
).map{"import " + _ + "._"}.mkString("\n")

externalResolvers ~= { _.filterNot{_.name.contains("Scala-Tools")} }

resolvers ++= Seq(
  "http://fornax-platform.org/nexus/content/groups/public/",
  "https://oss.sonatype.org/content/repositories/releases/"
).map{u => u at u}

libraryDependencies ++= Seq(
   "log4j" % "log4j" % "1.2.16" % "compile",
   "org.eclipse.xtend2" % "xtend-maven-plugin" % "2.2.2"
)

publishTo := sys.env.get("MAVEN_DIRECTORY").map{ dir =>
  Resolver.file("gh-pages",file(dir))(Patterns(true, Resolver.mavenStyleBasePattern))
}
