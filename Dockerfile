FROM java:8-alpine

MAINTAINER Ed Sweeney <ed@onextent.com>

RUN mkdir -p /app

COPY target/scala-2.12/*.jar /app/

WORKDIR /app

CMD java -jar ./IotServerDemo.jar
# override CMD from your run command, or k8s yaml, or marathon json, etc...

