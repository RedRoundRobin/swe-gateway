#!/bin/sh
cd gateway

mvn clean package

mv ./target/gateway-*.jar ../../gateway.jar
mv ./target/device-*.jar ../../devicesimulator.jar
cd ../..
rm -rf tmp 

java -jar /usr/src/gateway/gateway.jar &
java -jar /usr/src/gateway/devicesimulator.jar

echo "Gateway started..."
