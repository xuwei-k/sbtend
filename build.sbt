sbtPlugin := true

name := "sbtend"

organization := "com.github.xuwei_k"

description := "sbt xtend plugin"

version := "0.1.5-SNAPSHOT"

homepage := Some(url("https://github.com/xuwei-k/sbtend"))

licenses := Seq("MIT License" -> url("https://github.com/xuwei-k/sbtend/blob/master/LICENSE.txt"))

scalacOptions := Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
val xtendVersion = "2.4.2"
Seq(
   "org.eclipse.xtend" % "org.eclipse.xtend.lib" % xtendVersion,
   "org.eclipse.xtext" % "org.eclipse.xtext.xbase.lib" % xtendVersion,
   "org.eclipse.xtend" % "org.eclipse.xtend.standalone" % xtendVersion,
   "org.eclipse.emf" % "codegen" % "2.2.3"
)
}

publishTo := sys.env.get("MAVEN_DIRECTORY").map{ dir =>
  Resolver.file("gh-pages",file(dir))(Patterns(true, Resolver.mavenStyleBasePattern))
}

