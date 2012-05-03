# sbt xtend plugin [![Build Status](https://secure.travis-ci.org/xuwei-k/sbtend.png)](http://travis-ci.org/xuwei-k/sbtend)

http://www.eclipse.org/xtend/

`project/project/plugins.scala`

```scala
import sbt._

object Plugins extends Build {
  lazy val root = Project("root", file(".")) dependsOn(
    uri("git://github.com/xuwei-k/sbtend.git")
  )
}
```

your `build.sbt`

```
sbtendSettings
```
