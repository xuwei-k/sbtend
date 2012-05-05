package sbtend

import sbt._
import Keys._
import org.eclipse.xtext.xtend2.compiler.batch.Xtend2BatchCompiler
import org.apache.log4j.BasicConfigurator
import org.eclipse.xtext.xtend2.Xtend2StandaloneSetup

object Plugin extends sbt.Plugin{

  object SbtendKeys{
    lazy val xtendSource          = SettingKey[File]("xtend-source")
    lazy val xtendCompile         = SettingKey[Task[Seq[File]]]("xtend-compile")
    lazy val xtendOutputDirectory = SettingKey[File]("xtend-output-directory")
    lazy val xtendVersion         = SettingKey[String]("xtend-version")
    lazy val xtendWatchSources    = SettingKey[Boolean]("xtend-watch-sources")
  }

  import SbtendKeys._

  lazy val sbtendSettings:Seq[sbt.Project.Setting[_]] = Seq(
    includeFilter in unmanagedSources ~= { _ || GlobFilter("*.xtend") },
    resolvers += "xtend" at "http://build.eclipse.org/common/xtend/maven/",
    xtendVersion := "2.2.1",
    libraryDependencies <+= (xtendVersion){
      "org.eclipse.xtend2" % "org.eclipse.xtend2.lib" % _
    }
  ) ++ Seq(Compile,Test).flatMap{createSettings}

  private[sbtend] def createSettings(conf:sbt.Configuration):Seq[sbt.Project.Setting[_]] = Seq(
    xtendSource in conf <<= (sourceDirectory in conf)(_ / "xtend"),
    xtendOutputDirectory in conf <<= (sourceManaged in conf),
    xtendCompile in conf <<= (xtendOutputDirectory in conf,xtendSource in conf,dependencyClasspath in conf,classDirectory in conf,streams).map{
      (out,in,cp,classes,s) =>
      if(in.exists){
        if(! compileXtend(out,in,cp.map{_.data},classes,s.log)){
          throw new Error("xtend compile fail")
        }
        val javaFiles = (out ** "*.java" get)
        javaFiles.foreach{f =>
          s.log.info(f.toString)
          IO.readLines(f).foreach{ l => s.log.info(l) }
        }
        javaFiles
      } else Nil
    },
    sourceGenerators in conf <+= (xtendCompile in conf),
    watchSources <++= (xtendSource in conf).map{ dir =>
      (dir ** "*.xtend").get
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

