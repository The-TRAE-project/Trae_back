FROM openjdk:17-alpine
RUN apk add dumb-init
RUN mkdir /app
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
COPY target/*.jar /app/trae_backend.jar
WORKDIR /app
RUN chown -R javauser:javauser /app
USER javauser
EXPOSE 8088
#CMD "dumb-init" "java" "-jar" "trae_backend.jar"
ENTRYPOINT ["dumb-init","java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-jar","trae_backend.jar"]
