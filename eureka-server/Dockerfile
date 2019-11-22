FROM adoptopenjdk/openjdk11:jdk-11.0.3_7

RUN mkdir /opt/eureka-server
COPY target/eureka-server-1.0-SNAPSHOT.jar /opt/eureka-server/eureka-server.jar
CMD ["java", "-jar", "/opt/eureka-server/eureka-server.jar"]
