FROM adoptopenjdk/maven-openjdk11:latest
COPY . /usr/src/gateway/tmp
WORKDIR /usr/src/gateway
CMD ["sh", "start.sh"]
