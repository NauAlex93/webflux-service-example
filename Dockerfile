FROM registry.sigma.webfluxExample.ru/base/webfluxExampleworks/rhel7-openjdk1.8-jre:1.8.0_212
LABEL MAINTAINER="Dmitriy Polovinkin <Polovinkin.D.Y@webfluxExample.ru>"
ENV MAX_RAM_PERCENTAGE=60.0
COPY app.jar /app.jar
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=${MAX_RAM_PERCENTAGE} -jar /app.jar"]