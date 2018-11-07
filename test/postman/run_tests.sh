#!/bin/bash
basedir=$1
testdir=$2
echo "Starting docker container"
docker build -t="money-transfer-retailer-test-server" ${basedir}/target
docker run -d -p 8080:8080 --name money-transfer-retailer-test-server_container money-transfer-retailer-test-server
/git/circlecitools/bin/waitForServer.sh localhost:8080 5000
${testdir}/run_newman.sh ${testdir}
rc=$?
echo "Cleaning up Docker"
docker stop money-transfer-retailer-test-server_container
docker rm money-transfer-retailer-test-server_container
docker rmi money-transfer-retailer-test-server
exit $rc