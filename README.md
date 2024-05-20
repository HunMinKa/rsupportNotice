# 알서포트 공지사항 REST-API


# Features
- 공지사항 생성
- 공지사항 수정
- 공지사항 삭제
- 공지사항 조회
- 조회수 증가

# Technology Stack
- Java 21
- Spring Boot 3.2.4
- H2
- Redis
- Spring Data JPA (Hibernate)
- Spring Cache

# Core Strategy
- **조회수 증가 및 조회 정보 처리**: 스프링 캐쉬와 레디스를 활용하여 대용량 트래픽을 효율적으로 처리합니다. 공지사항 조회 시 조회수를 Redis에 캐쉬하고 일정 주기로 MySQL에 반영합니다.
  - 추가 설명: 캐싱 전략과 레디스 활용
    - 캐싱 전략의 세부 내용
       - 공지사항 조회 시 캐시를 사용하여 조회수를 관리합니다. 클라이언트의 요청에 대한 응답 속도를 향상시키기 위해 Redis를 사용하여 조회수를 캐시합니다. 이를 통해 매번 데이터베이스에 접근하지 않고도 빠르게 조회수를 제공할 수 있습니다.

    - 레디스 사용 방법
      - 레디스는 메모리 기반의 키-값 저장소로서 빠른 읽기 및 쓰기 속도를 제공합니다. 우리는 레디스의 장점을 활용하여 조회수 데이터를 메모리에 캐시하고, 이를 통해 고성능의 조회수 관리를 구현합니다. 또한 레디스의 데이터 구조를 활용하여 조회수를 저장하고 관리하는 방법에 대해서도 고려합니다.

    - 일정 주기로 MySQL에 반영하는 방법
      - 캐시된 조회수는 일정 시간이 지난 후에 MySQL에 반영됩니다. 이를 위해 배치 작업이나 스케줄링된 프로세스를 사용하여 주기적으로 Redis에서 조회수를 읽어와서 데이터베이스에 업데이트합니다. 이를 통해 조회수 정보가 항상 최신 상태로 유지되며, 동시에 데이터베이스에 지나치게 많은 부하가 가해지지 않도록 합니다.

    - 성능 최적화 전략
      - 조회수 캐싱과 데이터베이스 업데이트 과정을 통해 서비스의 성능을 최적화합니다. 특히, 빈번하게 요청되는 조회수 정보에 대한 캐싱을 통해 데이터베이스 부하를 줄이고 응답 속도를 향상시킵니다. 또한 일정 주기로 데이터베이스에 반영되는 업데이트 과정을 통해 데이터의 일관성을 유지하면서도 성능을 최적화할 수 있습니다.
- **성능 최적화**: 파일 업로드 서비스의 성능을 최적화하기 위해 비동기 처리와 멀티스레딩을 사용합니다.
- **확장성**: 모듈화된 구조를 통해 서비스가 확장될 수 있도록 설계되었습니다.

# Test Strategy
- **테스트 프레임워크**: JUnit 5, Mockito
- **테스트 커버리지**: 모든 주요 기능에 대해 단위 테스트 및 통합 테스트를 작성하였습니다.
- **테스트 실행**: Gradle을 통해 테스트를 실행할 수 있으며, IntelliJ IDEA에서 손쉽게 실행할 수 있도록 설정되어 있습니다.

# **Modules**

## Multi-Module Project Structure
This project is structured as a multi-module Gradle project to maintain modularity and separation of concerns.

## Core
Each submodule of this module is responsible for one domain service.

This must make the modular structure grow with the growth of the service.

### core:core-api
It is the only executable module in the project. It is structured to have domains to maximize initial development productivity.

It is also responsible for providing APIs and setting up frameworks for services.

### core:core-enum

This module contains enums that are used by `core-api` and must be delivered to external modules.

<br/>

## Storage
Submodules of this module are responsible for integrating with the various storages.

### storage:db-core*
*This module shows an example of connecting to `H2` using `Spring-Data-JPA`.

<br/>

## Support
Submodules of this module are responsible for additional support.

### support:logging
This module supports logging of service and has a dependency added for distributed tracing support.

It also includes dependencies to support `Sentry`.

### support:monitoring
This module supports monitoring of services.

<br/>

<br/>

# Dependency Management
All dependency versioning is done through `gradle.properties` file.

If you want to add a new dependency, put the version in `gradle.properties` and load it in `build.gradle`.

<br/>

## IntelliJ IDEA
This setting makes it easier to run the `test code` out of the box.

```
// Gradle Build and run with IntelliJ IDEA
Build, Execution, Deployment > Build Tools > Gradle > Run tests using > IntelliJ IDEA	
```

If you want to apply lint settings to the format of IDEA, please refer to the guide below.

[Spring Java Format IntelliJ IDEA](https://github.com/spring-io/spring-javaformat#intellij-idea)

---
# Running the Application

## Prerequisites
- Java 21
- Gradle 7.4 or later

## Steps to Run

1. **Clone the repository**:
    ```sh
    git clone <repository-url>
    cd <repository-directory>
    ```

2. **Build the project**:
    ```sh
    ./gradlew clean build
    ```

3. **Run the application**:
    ```sh
    ./gradlew :core:core-api:bootRun
    ```

The application will start and be accessible at `http://localhost:8080`.

## Running Tests

1. **Execute unit tests**:
    ```sh
    ./gradlew test
    ```

2. **Execute integration tests**:
    ```sh
    ./gradlew integrationTest
    ```
---
# Supported By
<div align="center"><a href="https://jb.gg/OpenSourceSupport"><img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" alt="JetBrains Logo (Main) logo." width="240"></a></div>

