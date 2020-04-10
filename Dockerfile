# docker run --run -d -p 9999:9999 rrr/api
FROM adoptopenjdk/maven-openjdk11:latest
COPY . /usr/src/gateway/tmp
EXPOSE 9999
WORKDIR /usr/src/gateway/tmp
CMD ["sh", "start.sh"]
