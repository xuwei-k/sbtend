#!/bin/sh

rm ls.sbt
rm scripted.sbt
rm project/ls.sbt
rm project/plugins.sbt

for v in 0.11.2 0.11.3 0.12.0
do
  sbt -sbt-version $v publish
done

git checkout .
