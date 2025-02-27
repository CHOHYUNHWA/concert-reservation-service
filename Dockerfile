# Base image 선택
FROM openjdk:17-jdk-slim

# 작업 디렉토리 생성
ARG JAR_FILE=build/libs/*.jar

# 빌드된 JAR 파일을 컨테이너로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너에서 실행할 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]