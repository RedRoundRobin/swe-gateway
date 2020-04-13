#!/bin/sh
cd gateway

mvn clean package

mv ./target/gateway-*.jar ../../gateway.jar
cd ../..
rm -rf tmp 

java -jar /usr/src/gateway/gateway.jar --server.port=9999

echo "Gateway started..."
