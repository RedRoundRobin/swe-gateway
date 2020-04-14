#!/bin/sh
cd gateway


if [ ! -f /usr/src/gateway/gateway.jar -eq 1 ] || [ ! -f /usr/src/gateway/devicesimulator.jar -eq 2 ]; then 
mvn clean package

mv ./target/gateway*.jar ../../gateway.jar
mv ./target/device*.jar ../../devicesimulator.jar

cd ../..
rm -rf tmp 
fi

java -jar /usr/src/gateway/gateway.jar &
java -jar /usr/src/gateway/devicesimulator.jar

echo "Gateway started with simulator..."
