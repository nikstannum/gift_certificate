FROM openjdk:17-alpine
LABEL maintainer="Semeniuk N.L."
WORKDIR /app
COPY ./build/libs/*.jar /app/
RUN apk update && apk add --no-cache bash
ENTRYPOINT ["java", "-jar"]
CMD ["gift_certificate-1.0-SNAPSHOT.jar"]