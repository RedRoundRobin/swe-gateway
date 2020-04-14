#!/bin/sh
cd gateway

GATEWAY=/usr/src/gateway/gateway.jar
DEVICE_SIMULATOR=/usr/src/gateway/devicesimulator.jar

if [ ! -f "$GATEWAY" ] || [ ! -f "$DEVICE_SIMULATOR" ]; then 
mvn clean package

mv ./target/gateway*.jar ../../gateway.jar
mv ./target/device*.jar ../../devicesimulator.jar

cd ../..
rm -rf tmp 
fi

java -jar /usr/src/gateway/gateway.jar &
java -jar /usr/src/gateway/devicesimulator.jar

echo "Gateway started with simulator..."
