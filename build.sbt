import sbtend.Versions
import xerial.sbt.Sonatype.SonatypeKeys

xerial.sbt.Sonatype.sonatypeRootSettings

sbtPlugin := true

name := "sbtend"

organization := "com.github.xuwei-k"

description := "sbt xtend plugin"

homepage := Some(url("https://github.com/xuwei-k/sbtend"))

licenses := Seq("MIT License" -> url("https://github.com/xuwei-k/sbtend/blob/master/LICENSE.txt"))

scalacOptions := Seq("-deprecation", "-unchecked")

scalacOptions in (Compile, doc) ++= {
  val tag = if(isSnapshot.value) gitHash else { "v" + version.value }
  Seq(
    "-sourcepath", (baseDirectory in LocalRootProject).value.getAbsolutePath,
    "-doc-source-url", s"https://github.com/xuwei-k/sbtend/tree/${tag}€{FILE_PATH}.scala"
  )
}

pomPostProcess := { node =>
  import scala.xml._
  import scala.xml.transform._
  def stripIf(f: Node => Boolean) = new RewriteRule {
    override def transform(n: Node) =
      if (f(n)) NodeSeq.Empty else n
  }
  val stripTestScope = stripIf { n => n.label == "dependency" && (n \ "scope").text == "test" }
  new RuleTransformer(stripTestScope).transform(node)(0)
}

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

def gitHash = scala.util.Try(
  sys.process.Process("git rev-parse HEAD").lines_!.head
).getOrElse("master")

pomExtra := (
<developers>
  <developer>
    <id>xuwei-k</id>
    <name>Kenji Yoshida</name>
    <url>https://github.com/xuwei-k</url>
  </developer>
</developers>
<scm>
  <url>git@github.com:xuwei-k/sbtend.git</url>
  <connection>scm:git:git@github.com:xuwei-k/sbtend.git</connection>
  <tag>{if(isSnapshot.value) gitHash else { "v" + version.value }}</tag>
</scm>
)

releaseProcess := {

import sbtrelease.ReleaseStateTransformations._

Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(
    action = { state =>
      val extracted = Project extract state
      extracted.runAggregated(PgpKeys.publishSigned in Global in extracted.get(thisProjectRef), state)
    },
    enableCrossBuild = true
  ),
  setNextVersion,
  commitNextVersion,
  ReleaseStep{ state =>
    val extracted = Project extract state
    extracted.runAggregated(SonatypeKeys.sonatypeReleaseAll in Global in extracted.get(thisProjectRef), state)
  },
  pushChanges
)
}
