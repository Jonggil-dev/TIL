# Spring Dockerfile

```dockerfile
# 1단계: 빌드 환경 준비
FROM openjdk:17-jdk-slim as build

# 작업 디렉토리 설정
WORKDIR /app

# 필요한 파일들 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 프로젝트 빌드
RUN ./gradlew build -x test

# 2단계: 실행 환경 준비
FROM openjdk:17-jre-slim

WORKDIR /app

# 1단계에서 생성된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8888
# 애플리케이션 실행
ENTRYPOINT ["java","-jar","app.jar"]
```

- **`FROM openjdk:17-jdk-slim as build`** : 이 이미지에는 Java 17 JDK가 포함되어 있으며, 애플리케이션 빌드에 필요한 모든 JDK 도구를 제공합니다. `slim` 버전은 용량이 작아 빌드 속도가 빨라집니다.

- **`COPY gradlew(or gradlew.bat), gradle`**
  - **`gradlew`** (Linux/macOS용)와 **`gradlew.bat`** (Windows용): Gradle Wrapper 실행 스크립트입니다. 이 스크립트를 사용하면 Gradle이 설치되어 있지 않은 환경에서도 프로젝트를 빌드할 수 있습니다. Gradle Wrapper를 사용하는 것이 권장되는 방식이며, 이를 통해 프로젝트가 특정 버전의 Gradle을 사용하여 빌드 되도록 보장할 수 있습니다.
  
  - **`gradle`**: `gradle/wrapper/gradle-wrapper.properties`를 Gradle Wrapper의 버전과 다운로드 URL을 지정하는 파일입니다. `gradlew` 스크립트를 실행할 때 이 파일에 지정된 Gradle 버전을 자동으로 다운로드하고 사용합니다.
  
  - `gradlew`를 복사하지 않고 빌드하는 경우는 Docker 빌드 환경에 이미 필요한 Gradle 버전이 설치되어 있거나, 다른 방식(예: Gradle 베이스 이미지를 사용하는 경우)으로 Gradle 빌드 환경을 설정한 경우일 수 있습니다. 그러나 일반적으로는 Gradle Wrapper를 사용하여 프로젝트와 동일한 Gradle 버전을 보장하는 것이 좋습니다.
  - 일관된 빌드 환경을 위해 `gradlew` 및 관련 파일을 포함시키는 것이 좋습니다. `gradlew`를 포함시키지 않는 경우에는 빌드 환경에 이미 Gradle이 적절하게 설정되어 있어야 합니다.
  
- **`FROM openjdk:17-jdk-slim as build`** : 이 이미지는 Java 17 JRE만 포함하고 있어, 애플리케이션 실행에 필요한 최소 환경을 제공합니다.

- **`/app/build/libs/\*.jar`**: 이 경로는 이전 빌드 스테이지 내에서 JAR 파일이 위치한 경로를 지정합니다. `*.jar`는 모든 JAR 파일을 대상으로 하며, 실제 빌드 프로세스에서 생성된 JAR 파일의 이름이 무엇이든 상관없이 모든 JAR 파일을 선택합니다.
- **`app.jar`**: 이 부분은 최종 이미지 내에서 복사된 JAR 파일의 목적지 이름을 지정합니다. 이 경우, 빌드 스테이지에서 복사된 모든 JAR 파일이 `app.jar`라는 단일 파일명으로 최종 이미지 내에 저장됩니다. 만약 여러 JAR 파일이 있을 경우, Docker 빌드는 에러를 발생시킬 것입니다, 그래서 실제 사용 시에는 특정 JAR 파일명을 명확히 지정하거나, 실제로 단일 JAR 파일만 생성되도록 해야 합니다.