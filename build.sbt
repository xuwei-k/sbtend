import sbtend.Versions

sbtPlugin := true

name := "sbtend"

scalaVersion := "2.10.5"

organization := "com.github.xuwei_k"

description := "sbt xtend plugin"

version := "0.1.6-SNAPSHOT"

homepage := Some(url("https://github.com/xuwei-k/sbtend"))

licenses := Seq("MIT License" -> url("https://github.com/xuwei-k/sbtend/blob/master/LICENSE.txt"))

scalacOptions := Seq("-deprecation", "-unchecked")

// add shared code to build
unmanagedSourceDirectories in Compile += baseDirectory.value / "project/src/main"

// regrettably, xtend dependencies would not resolve without disabling checksums
checksums in update := Nil

libraryDependencies ++= {
  Seq(
     "org.eclipse.xtend" % "org.eclipse.xtend.lib" % Versions.xtend,
     "org.eclipse.xtext" % "org.eclipse.xtext.xbase.lib" % Versions.xtend,
     "org.eclipse.xtend" % "org.eclipse.xtend.core" % Versions.xtend,
     "org.eclipse.emf" % "codegen" % "2.2.3"
  )
}

publishTo := sys.env.get("MAVEN_DIRECTORY").map { dir =>
  Resolver.file("gh-pages",file(dir))(Patterns(true, Resolver.mavenStyleBasePattern))
}

