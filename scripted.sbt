scriptedLaunchOpts ++= {
  import scala.collection.JavaConverters._
  val args = Seq("-Xmx","-Xms")
  management.ManagementFactory.getRuntimeMXBean().getInputArguments().asScala.filter(a => args.contains(a) || a.startsWith("-XX")).toSeq
}
