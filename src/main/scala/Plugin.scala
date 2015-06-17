package sbtend

import sbt._
import Keys._
import java.io.{Writer,File}
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler
import org.apache.log4j.BasicConfigurator
import org.eclipse.xtend.core.XtendStandaloneSetup
import sbt.plugins.JvmPlugin
import java.io.CharArrayWriter

object Plugin extends sbt.AutoPlugin {

  override def requires = JvmPlugin

  override def trigger = allRequirements

  override val projectSettings = sbtendSettings

  object autoImport {
    lazy val xtendSource          = settingKey[File]("directory for xtend source")
    lazy val xtendCompile         = taskKey[Seq[File]]("compiles xtend sources")
    lazy val xtendOutputDirectory = settingKey[File]("directory for generated java source")
    lazy val xtendVersion         = settingKey[String]("version of xtend libraries")
    lazy val xtendWatchSources    = settingKey[Boolean]("add xtend files to list of watched files")
    lazy val xtendLogOutput       = settingKey[Boolean]("print generated java code to console")
  }

  import autoImport._

  lazy val sbtendSettings: Seq[Setting[_]] = Seq(
    includeFilter in unmanagedSources ~= { _ || GlobFilter("*.xtend") },
    xtendVersion := Versions.xtend,
    libraryDependencies += "org.eclipse.xtend" % "org.eclipse.xtend.lib" % xtendVersion.value
  ) ++ Seq(Compile, Test).flatMap(createSettings)

  private[sbtend] def createSettings(conf: Configuration): Seq[Setting[_]] = Seq(
    xtendSource in conf <<= (sourceDirectory in conf)(_ / "xtend"),
    xtendOutputDirectory in conf <<= (sourceManaged in conf),
    xtendLogOutput := false,
    xtendCompile in conf := {
      val classes = (classDirectory in conf).value
      val cp = (dependencyClasspath in conf).value
      val out = (xtendOutputDirectory in conf).value
      val in = (xtendSource in conf).value
      val log = streams.value.log

      if (in.exists) {
        if(!compileXtend(out, in, cp.map(_.data), classes, log)) {
          throw new Error("xtend compile fail")
        }
        val javaFiles = (out ** "*.java" get)
        if (xtendLogOutput.value) {
          javaFiles.foreach { f =>
            log.info(f.toString)
            IO.readLines(f).foreach(log.info(_))
          }
        }
        javaFiles
      } else Nil
    },
    sourceGenerators in conf <+= (xtendCompile in conf),
    watchSources ++= ((xtendSource in conf).value ** "*.xtend").get
  )

  def createLogger():Writer = {
    import org.apache.log4j.{Logger => Log4jLogger,Level,WriterAppender,SimpleLayout,Layout}
    val logger = Log4jLogger.getLogger("org.eclipse.xtext")
    logger.setAdditivity(false)
    logger.setLevel(Level.DEBUG)
    logger.removeAllAppenders()
    val w = new CharArrayWriter
    val appender = new WriterAppender(new SimpleLayout(),w)
    logger.addAppender(appender)
    w
  }

  private[sbtend] def compileXtend(out: File,in: File,cp: Seq[File], classes: File, log: Logger):Boolean = {
    val logger = createLogger()
    val injector = new XtendStandaloneSetup().createInjectorAndDoEMFRegistration
    val c = injector.getInstance(classOf[XtendBatchCompiler])
    c.setOutputPath(out.toString())
    c.setSourcePath(in.toString())
    c.setVerbose(true)
    c.setOutputWriter(new LoggerWriter(log))
    c.setClassPath(cp.map(_.getAbsolutePath).mkString(File.pathSeparator))
    c.compile()
  }
}
