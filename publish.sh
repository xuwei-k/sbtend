#!/bin/sh

rm ls.sbt
rm scripted.sbt
rm project/ls.sbt
rm project/plugins.sbt

for v in 0.12.3 0.13.0
do
  sbt -sbt-version $v publish
done

git checkout .
