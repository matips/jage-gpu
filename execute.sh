#!/bin/env bash
#PBS -l walltime=00:00:30
#PBS -A plgmatips2015b
#PBS -q gpgpu
#PBS -l nodes=1:ppn=1:gpus=1:exclusive_process

cd /people/plgmatips/jage-gpu-examples
module load apps/java/1.8.0

echo "AddingApplication"
java -Xms1024m -cp jage-gpu-examples-1.1-SNAPSHOT-jar-with-dependencies.jar:resources pl.edu.agh.jage.gpu.examples.adding.AddingApplication
echo "AddingApplicationGPU"
java -Xms1024m -cp jage-gpu-examples-1.1-SNAPSHOT-jar-with-dependencies.jar:resources pl.edu.agh.jage.gpu.examples.adding.AddingApplicationGPU
echo "IntegralApplication"
java -Xms1024m -cp jage-gpu-examples-1.1-SNAPSHOT-jar-with-dependencies.jar:resources pl.edu.agh.jage.gpu.examples.integrals.IntegralApplication
echo "IntegralApplicationGPU"
java -Xms1024m -cp jage-gpu-examples-1.1-SNAPSHOT-jar-with-dependencies.jar:resources pl.edu.agh.jage.gpu.examples.integrals.IntegralApplicationGPU