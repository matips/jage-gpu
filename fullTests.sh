#!/bin/env bash
#PBS -l walltime=00:00:30
#PBS -A bigdata1
#PBS -q gpgpu
#PBS -l nodes=1:ppn=1:gpus=1:exclusive_process

cd /people/plgmatips/jage-gpu-examples
module load apps/java/1.8.0
RESULTS_DIR=results
mkdir -p ${RESULTS_DIR}

source test.sh

addingCPU 1
addingGPU 1

addingCPU 20
addingGPU 20

addingCPU 60
addingGPU 60

addingCPU 80
addingGPU 80

addingCPU 100
addingGPU 100

addingCPU 150
addingGPU 150

addingCPU 220
addingGPU 220

addingCPU 300
addingGPU 300

addingCPU 400
addingGPU 400

addingCPU 600
addingGPU 600

addingCPU 1000
addingGPU 1000

#integralGPU 1
#integralGPU 20
#integralGPU 60
#integralGPU 80
#integralGPU 100
#integralGPU 150
#integralGPU 220
#integralGPU 300
#integralGPU 400
#integralGPU 600
#integralGPU 1000
#
#integralCPU 1
#integralCPU 20
#integralCPU 60
#integralCPU 80
#integralCPU 100
#integralCPU 150
#integralCPU 220
#integralCPU 300

printHeaders
publishResults


