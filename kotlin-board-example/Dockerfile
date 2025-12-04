# ===================================
# Stage 1: Build Stage (Gradle 빌드)
# ===================================
FROM gradle:8.5-jdk17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 설정 파일 먼저 복사 (캐시 활용)
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY gradlew ./

# 의존성 다운로드 (캐시 레이어 생성)
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드 (테스트 제외)
RUN ./gradlew bootJar --no-daemon -x test

# ===================================
# Stage 2: Runtime Stage (실행 환경)
# ===================================
FROM eclipse-temurin:17-jre

# 메타데이터
LABEL maintainer="your-email@example.com"
LABEL description="Kotlin Board API with Spring Boot"
LABEL version="1.0.0"

# 작업 디렉토리
WORKDIR /app

# 빌드 스테이지에서 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 헬스체크 (30초마다 확인)
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 애플리케이션 실행
# -Dspring.profiles.active: 실행 프로파일 지정
# -Xmx512m: 최대 메모리 512MB
ENTRYPOINT ["java", \
    "-Dspring.profiles.active=prod", \
    "-Xmx512m", \
    "-XX:+UseContainerSupport", \
    "-jar", \
    "app.jar"]
