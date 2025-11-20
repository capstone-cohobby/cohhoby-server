# Dockerfile (개선안 - 외부에서 만든 JAR 사용 유지)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} /app.jar

FROM eclipse-temurin:21-jre-alpine
ARG GIT_SHA=unknown
LABEL org.opencontainers.image.revision=$GIT_SHA
ENV COMMIT_SHA=$GIT_SHA
RUN apk add --no-cache gcompat
COPY --from=builder /app.jar /app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=local","-jar","/app.jar"]


