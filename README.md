# sbt xtend plugin [![Build Status](https://secure.travis-ci.org/xuwei-k/sbtend.png)](http://travis-ci.org/xuwei-k/sbtend)

http://www.eclipse.org/xtend/

## setup

`project/project/plugins.scala`

```scala
import sbt._

object Plugins extends Build {
  lazy val root = Project("root", file(".")) dependsOn(
    uri("git://github.com/xuwei-k/sbtend.git#XX") // where XX is tag or SHA1
  )
}
```

or

`project/plugins.sbt`

```scala
addSbtPlugin("com.github.xuwei_k" % "sbtend" % "XX") // where XX is version

resolvers += "xuwei-k maven repo" at "http://xuwei-k.github.io/mvn/"
```

and your `build.sbt`

```
sbtendSettings
```
