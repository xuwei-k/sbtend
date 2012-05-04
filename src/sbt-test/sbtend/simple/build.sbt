import SbtendKeys._

sbtendSettings

scalaVersion := "2.9.1"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.1.3"

TaskKey[Unit]("write-illegal-file") <<= (xtendSourceDirectory).map{ dir =>
  IO.write(dir / "illegal.xtend","shoud be compile fail")
}

