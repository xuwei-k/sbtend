package sbtend

import sbt._
import Keys._
import java.io.{Writer,File}
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler
import org.apache.log4j.BasicConfigurator
import org.eclipse.xtend.core.XtendStandaloneSetup

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
    xtendVersion := "2.4.2",
    libraryDependencies <+= (xtendVersion){
      "org.eclipse.xtend" % "org.eclipse.xtend.lib" % _
    }
  ) ++ Seq(Compile,Test).flatMap{createSettings}

  private[sbtend] def createSettings(conf:sbt.Configuration):Seq[sbt.Project.Setting[_]] = seq(
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

  def createLogger():Writer = {
    import org.apache.log4j.{Logger => Log4jLogger,Level,WriterAppender,SimpleLayout,Layout}

    val logger = Log4jLogger.getLogger("org.eclipse.xtext")
    logger.setAdditivity(false)
    logger.setLevel(Level.DEBUG)
    logger.removeAllAppenders()
    val w = new java.io.CharArrayWriter
    val appender = new WriterAppender(new SimpleLayout(),w)
    logger.addAppender(appender)
    w
  }

  private[sbtend] def compileXtend(out:File,in:File,cp:Seq[File],classes:File,log:Logger):Boolean = {

    val logger = createLogger()
    val injector = new XtendStandaloneSetup().createInjectorAndDoEMFRegistration
    val c = injector.getInstance(classOf[XtendBatchCompiler])
    c.setOutputPath(out.toString())
    c.setSourcePath(in.toString())
    c.setVerbose(true)
    c.setOutputWriter(new LoggerWriter(log))
    c.setClassPath(cp.map{_.getAbsolutePath}.mkString(File.pathSeparator))
    c.compile()
  }
}

