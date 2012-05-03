import SbtendKeys._

sbtendSettings

scalaVersion := "2.9.1"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "6.0.4"

TaskKey[Unit]("write-illegal-file") <<= (xtendSourceDirectory).map{ dir =>
  IO.write(dir / "illegal.xtend","shoud be compile fail")
}

