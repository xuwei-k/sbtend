ScriptedPlugin.scriptedSettings

ScriptedPlugin.scriptedBufferLog := false

watchSources <++= (sbtTestDirectory).map{ dir => (dir ***).get }

scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("-Xmx","-Xms").exists(a.startsWith) || a.startsWith("-XX")
)
