package sbtend

import sbt._
import Keys._
import org.eclipse.xtext.xtend2.compiler.batch.Main

object Plugin extends sbt.Plugin{

  object SbtendKeys{
    lazy val xtendSourceDirectory = SettingKey[File]("xtend-source-directory")
    lazy val xtendCompile         = SettingKey[Task[Seq[File]]]("xtend-compile")
    lazy val xtendCompileOptions  = TaskKey[Seq[String]]("xtend-compile-options")
    lazy val xtendOutputDirectory = SettingKey[File]("xtend-output-directory")
    lazy val xtendVersion         = SettingKey[String]("xtend-version")
  }

  import SbtendKeys._

  lazy val sbtendSettings:Seq[sbt.Project.Setting[_]] = Seq(
    xtendSourceDirectory <<= (sourceDirectory in Compile)(_ / "xtend"),
    xtendOutputDirectory <<= sourceManaged,
    xtendCompileOptions <<= (xtendOutputDirectory,xtendSourceDirectory,dependencyClasspath in Compile).map{
      (out,in,cp) =>
      Seq("-d",out.toString,in.toString) // ++ cp.flatMap{c => Seq("-cp",c.data.toString)} // TODO
    },
    xtendCompile <<= (xtendCompileOptions,xtendOutputDirectory).map{
      (opt,out) =>
      Main.main(opt.toArray)
      (out ** "*.java" get)
    },
    (sourceGenerators in Compile) <+= xtendCompile,
    resolvers += "xtend" at "http://build.eclipse.org/common/xtend/maven/",
    xtendVersion := "2.2.1",
    libraryDependencies <+= (xtendVersion){
      "org.eclipse.xtend2" % "org.eclipse.xtend2.lib" % _
    }
  )

}

