# 1단계 : 빌드 환경 (Java 21 JDK)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} /app.jar

# 2단계 : 실행 환경 (Java 21 JRE)
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache gcompat
COPY --from=builder /app.jar /app.jar

# EC2에서 application-local.yml을 사용하도록 'local' 프로필 활성화
ENTRYPOINT ["java", "-Dspring.profiles.active=local", "-jar", "/app.jar"]