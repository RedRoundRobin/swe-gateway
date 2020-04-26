FROM adoptopenjdk/maven-openjdk11:latest
RUN mkdir -p /usr/src/gateway/tmp
COPY start.sh /usr/src/gateway
COPY . /usr/src/gateway/tmp
WORKDIR /usr/src/gateway
CMD ["sh", "start.sh"]
