import SbtendKeys._

sbtendSettings

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.1.3",
  "com.novocode" % "junit-interface" % "0.9-RC2" % "test"
)

TaskKey[Unit]("write-illegal-file") <<= (xtendSource in Compile).map{ dir =>
  IO.write(dir / "illegal.xtend","shoud be compile fail")
}

