#!/usr/bin/env bash

HEADER="agent, time, size"
JAGE_GPU_DIR=`pwd -P`
function printHeaders(){
    cd -P ${JAGE_GPU_DIR}
    for f in `ls results/*.csv`; do
        FIRST_LINE=`cat $f | head --lines=1`
        if [ "x$FIRST_LINE" != "x$HEADER" ]; then
            echo "$HEADER" | cat - $f > temp
            mv temp $f
        fi
    done
}
function publishResults(){
    cd -P ${JAGE_GPU_DIR}
    cd ../4540050vyrxhf
    git reset --hard origin/master

    cd -P ${JAGE_GPU_DIR}
    cp -r results ../4540050vyrxhf
    cd ../4540050vyrxhf
    git add results/*
    git commit -m "push new results"
    git push
    cd ${JAGE_GPU_DIR}
}
function integralGPU(){
    java -DxSize=100 -DySize=$1 -Xms1024m -cp jage-gpu-examples-1.1-SNAPSHOT-jar-with-dependencies.jar:resources pl.edu.agh.jage.gpu.examples.integrals.IntegralApplicationGPU | grep "pl.edu.agh.jage.gpu" >> ${RESULTS_DIR}/intGPU.csv
}
function integralCPU(){
    java -DxSize=100 -DySize=$1  -Xms1024m -cp jage-gpu-examples-1.1-SNAPSHOT-jar-with-dependencies.jar:resources pl.edu.agh.jage.gpu.examples.integrals.IntegralApplication | grep "pl.edu.agh.jage.gpu"  >> ${RESULTS_DIR}/intCPU.csv
}
function addingGPU(){
    java -DxSize=100 -DySize=$1  -Xms1024m -cp jage-gpu-examples-1.1-SNAPSHOT-jar-with-dependencies.jar:resources pl.edu.agh.jage.gpu.examples.adding.AddingApplicationGPU  | grep "pl.edu.agh.jage.gpu"  >> ${RESULTS_DIR}/addGPU.csv
}
function addingCPU(){
   java -DxSize=100 -DySize=$1  -Xms1024m -cp jage-gpu-examples-1.1-SNAPSHOT-jar-with-dependencies.jar:resources pl.edu.agh.jage.gpu.examples.adding.AddingApplication | grep "pl.edu.agh.jage.gpu"  >> ${RESULTS_DIR}/addCPU.csv
}
