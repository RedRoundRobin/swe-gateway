FROM adoptopenjdk/maven-openjdk11:latest
COPY . /usr/src/gateway/tmp
WORKDIR /usr/src/gateway/tmp
CMD ["sh", "start.sh"]
