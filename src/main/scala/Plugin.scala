package sbtend

import sbt._
import Keys._
import org.eclipse.xtext.xtend2.compiler.batch.Xtend2BatchCompiler
import org.apache.log4j.BasicConfigurator
import org.eclipse.xtext.xtend2.Xtend2StandaloneSetup

object Plugin extends sbt.Plugin{

  object SbtendKeys{
    lazy val xtendSourceDirectory = SettingKey[File]("xtend-source-directory")
    lazy val xtendCompile         = SettingKey[Task[Seq[File]]]("xtend-compile")
    lazy val xtendOutputDirectory = SettingKey[File]("xtend-output-directory")
    lazy val xtendVersion         = SettingKey[String]("xtend-version")
    lazy val xtendWatchSources    = SettingKey[Boolean]("xtend-watch-sources")
  }

  import SbtendKeys._

  lazy val sbtendSettings:Seq[sbt.Project.Setting[_]] = Seq(
    xtendSourceDirectory <<= (sourceDirectory in Compile)(_ / "xtend"),
    xtendOutputDirectory <<= sourceManaged,
    xtendCompile <<= (xtendOutputDirectory,xtendSourceDirectory,dependencyClasspath in Compile,classDirectory in Compile,streams).map{
      (out,in,cp,classes,s) =>
      if(! compileXtend(out,in,cp.map{_.data},classes,s.log)){
        throw new Error("xtend compile fail")
      }
      val javaFiles = (out ** "*.java" get)
      javaFiles.foreach{f =>
        s.log.info(f.toString)
        IO.readLines(f).foreach{ l => s.log.info(l) }
      }
      javaFiles
    },
    (sourceGenerators in Compile) <+= xtendCompile,
    resolvers += "xtend" at "http://build.eclipse.org/common/xtend/maven/",
    xtendVersion := "2.2.1",
    libraryDependencies <+= (xtendVersion){
      "org.eclipse.xtend2" % "org.eclipse.xtend2.lib" % _
    },
    xtendWatchSources := true,
    watchSources <++= (xtendWatchSources,xtendSourceDirectory).map{ (watch,dir) =>
      if(watch) (dir ** "*.xtend").get
      else Nil
    }
  )

  private[sbtend] def compileXtend(out:File,in:File,cp:Seq[File],classes:File,log:Logger):Boolean = {
    BasicConfigurator.configure()
    val injector = new Xtend2StandaloneSetup().createInjectorAndDoEMFRegistration
    val c = injector.getInstance(classOf[Xtend2BatchCompiler])
    c.setOutputPath(out.toString())
    c.setSourcePath(in.toString())
    c.setVerbose(true)
    c.setClassPath(cp.map{_.getAbsolutePath}.mkString(":"))
    c.compile()
  }
}

