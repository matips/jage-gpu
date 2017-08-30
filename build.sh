#!/usr/bin/env bash

cd -P "$(dirname $0)"

mvn clean install
cd jage-gpu-examples
mvn org.apache.maven.plugins:maven-assembly-plugin:2.6:assembly
cd ..

scp -r jage-gpu-examples/src/main/  *.sh *.cl  jage-gpu-examples/target/jage-gpu-examples-1.1-SNAPSHOT-jar-with-dependencies.jar plgmatips@zeus.cyfronet.pl:/people/plgmatips/jage-gpu-examples
