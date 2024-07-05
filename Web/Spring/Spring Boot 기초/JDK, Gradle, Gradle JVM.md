

# JDK, Gradle, Gradle JVM

### 1. JDK (Java Development Kit)

- **역할과 의미**: JDK는 Java 애플리케이션 개발, 컴파일, 실행을 위한 핵심 도구 모음입니다. 이에는 Java 컴파일러(`javac`), 실행 환경(JRE), 개발에 필요한 라이브러리 및 도구들이 포함됩니다.
- **컴파일 과정에서의 역할**: Java 소스 코드를 바이너리 코드(.class 파일)로 변환하는 컴파일 작업은 JDK의 `javac` 컴파일러를 사용하여 수행됩니다. 이 과정은 직접 커맨드 라인에서 `javac`를 실행하거나, IntelliJ IDEA 같은 IDE에서 자동으로 처리할 수 있습니다.
- **IntelliJ 설정 영향**: 
  - IntelliJ IDEA에서는 `File > Project Structure > Project Settings > Project`를 통해 프로젝트의 JDK(또는 SDK)를 설정합니다. 
  - **이 JDK 설정은 프로젝트의 컴파일 및 실행에 사용되는 Java 버전을 결정합니다.**


### 2. Gradle

- **역할과 의미**: Gradle은 프로젝트의 빌드 및 관리를 자동화하는 빌드 시스템입니다. 이는 소스 코드 컴파일, 라이브러리 종속성 관리, 패키징, 테스팅, 배포 등의 작업을 자동화하고 구성합니다.
- **빌드 과정에서의 역할**: Gradle은 `build.gradle` 스크립트에 정의된 설정에 따라 빌드 과정을 관리합니다. 이때 Gradle은 내부적으로 JDK의 `javac` 컴파일러를 호출하여 소스 코드를 컴파일하며, 추가적인 빌드 태스크(라이브러리 종속성 관리, 패키징, 테스팅, 배포 등)를 수행합니다.
- **IntelliJ 설정 영향**: 
  - `프로젝트 디렉토리 내의 gradle/wrapper/gradle-wrapper.properties > distributionUrl`에서 gradle-x.x 버전을 설정할 수 있습니다.


### 3. Gradle JVM

- **역할과 의미**: Gradle JVM은 해당 Gradle 버전을 실행하는 데 필요한 Java 환경을 지정합니다. 
- **빌드 과정에서의 역할**: 이 설정은 Gradle 빌드 프로세스가 실행되는 JVM 환경을 결정합니다. Gradle이 Java 기반 애플리케이션인 만큼, Gradle 작업의 실행과 성능은 이 JVM 설정에 의존적입니다.
- **IntelliJ 설정 영향**
  - IntelliJ IDEA에서는 `Settings > Build, Execution, Deployment > Build Tools > Gradle`에서 Gradle 설정을 관리합니다.  
  - 여기서 프로젝트의 Gradle JVM(Gradle 작업 실행에 사용되는 JVM)을 설정할 수 있습니다.
  -  **JVM 설정은 Gradle 빌드 프로세스 자체가 실행되는 Java 환경을 결정하며, Gradle 스크립트 실행의 성능과 호환성에 영향을 미칩니다.**


### 요약

- 소스 코드 컴파일은 기본적으로 JDK의 컴파일러에 의해 수행되지만, Gradle과 같은 빌드 시스템을 사용하는 경우, 그러한 컴파일 과정은 Gradle에 의해 자동화되어 관리됩니다. Gradle은 빌드 과정을 조정하고, 필요에 따라 JDK의 컴파일러를 호출하여 소스 코드를 컴파일하는 역할을 합니다. 따라서, **실제 컴파일 작업은 JDK가 하지만, 그 과정을 관리하는 것은 Gradle, 그러한 Gradle이 실행되는 환경을 제공하는것은 Gradle JVM**이라고 할 수 있습니다.



### 4. 예시: JDK 17과 Gradle JVM 8 사용 시 발생할 수 있는 문제

**상황 설명**:

- **프로젝트 SDK(JDK)**: Java 17
- **Gradle JVM 버전**: Java 8

- **JDK와 Gradle JVM의 버전 차이에서 발생하는 문제**: JDK와 Gradle JVM 버전의 차이가 직접적으로 컴파일 문제를 유발하지 않는다는 점을 명확히 해야 합니다. 실제 문제는 빌드 과정에서 사용되는 특정 Gradle 플러그인이나 스크립트가 더 높은 버전의 Java를 요구할 때 발생합니다. **즉, 컴파일 자체는 문제가 없지만, 빌드 과정에서 Java 버전에 의존하는 기능을 사용할 경우 문제가 발생할 수 있습니다.**
- **컴파일 통과 후 빌드 과정 문제**:  소스 코드가 JDK 17에서만 사용 가능한 기능을 사용하고 있다면, 실제 코드 컴파일은 문제 없이 진행됩니다(프로젝트 SDK가 JDK 17로 설정되어 있을 경우). 하지만, Gradle 스크립트나 플러그인이 Java 8 (Gradle JVM 설정)에서는 실행되지 않는 기능을 사용하려고 할 때, 빌드 과정에서 문제가 발생할 수 있습니다. 이는 컴파일이 아닌, 빌드 과정(예: 테스트 실행, 패키징 등)에서의 문제입니다.
- **Gradle 스크립트**: build.gradle` 파일에 작성된 코드는 Gradle 스크립트라고 합니다. 이 스크립트는 프로젝트의 빌드 과정을 정의하며, 의존성 관리, 플러그인 설정, 컴파일 옵션 설정 등 다양한 빌드 관련 작업을 구성할 수 있습니다. Gradle 스크립트는 Groovy 또는 Kotlin DSL  (Kotlin 기반의 Gradle 스크립트, `build.gradle.kts` 파일에 사용됨)을 사용하여 작성할 수 있습니다.

