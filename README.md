# 정산 시스템

<br>

### 프로젝트 소개  
동영상 플랫폼의 조회수 데이터를 기반으로 통계와 정산을 처리하는 시스템입니다.

### 프로젝트 진행기간  

2024.10 ~ 2024.11 (4주)


**테스트 계정**  

> 정산시스템 사용자 
> - ID: lee  
> - PW: 1212  
> 
> 동영상 관리자 
> - ID: admin  
> - PW: 1212  



<br>

## 📋 실행방법  

1. **레포지토리 복제 후 의존성 설치**  

   ```bash
   $ git clone https://github.com/lee411806/settlement-platform.git
   $ cd settlement-platform

   코드로 실행
   $ ./gradlew build
   $ java -jar build/libs/settlement-platform.jar

   Intelij로 실행
   Tasks > build > build
   Tasks > application > bootRun

   
<br>


## 🛠 기술스택
[![Java][Java]][Java-url]
[![Spring Boot][SpringBoot]][SpringBoot-url]
[![Spring Batch][SpringBatch]][SpringBatch-url]
[![JPA][JPA]][JPA-url]
[![MySQL][MySQL]][MySQL-url]

[![Gradle][Gradle]][Gradle-url]
[![Docker Compose][DockerCompose]][DockerCompose-url]

[![JWT][JWT]][JWT-url]
[![OAuth2][OAuth2]][OAuth2-url]

[![JUnit][JUnit]][JUnit-url]
[![Mockito][Mockito]][Mockito-url]

[![Git][Git]][Git-url]
[![GitHub][GitHub]][GitHub-url]

<!-- Badge 이미지 링크 -->
[Java]: https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white
[SpringBoot]: https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white
[SpringBatch]: https://img.shields.io/badge/Spring%20Batch-4DC71F?style=for-the-badge&logo=spring&logoColor=white
[JPA]: https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=hibernate&logoColor=white
[MySQL]: https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white
[Gradle]: https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white
[DockerCompose]: https://img.shields.io/badge/Docker%20Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white
[JWT]: https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white
[OAuth2]: https://img.shields.io/badge/OAuth2-3E8E41?style=for-the-badge&logo=oauth&logoColor=white
[JUnit]: https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white
[Mockito]: https://img.shields.io/badge/Mockito-FFCA28?style=for-the-badge&logo=java&logoColor=white
[Git]: https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white
[GitHub]: https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white

<!-- 웹사이트 링크 -->
[Java-url]: https://www.oracle.com/java/
[SpringBoot-url]: https://spring.io/projects/spring-boot
[SpringBatch-url]: https://spring.io/projects/spring-batch
[JPA-url]: https://spring.io/projects/spring-data-jpa
[MySQL-url]: https://www.mysql.com/
[Gradle-url]: https://gradle.org/
[DockerCompose-url]: https://docs.docker.com/compose/
[JWT-url]: https://jwt.io/
[OAuth2-url]: https://oauth.net/2/
[JUnit-url]: https://junit.org/junit5/
[Mockito-url]: https://site.mockito.org/
[Git-url]: https://git-scm.com/
[GitHub-url]: https://github.com/



<br>

### ⚙️ 개발 환경


- **Java**: 17
- **MySQL**: 8.0
- **Gradle**: 8.10.2
- **Spring Boot**: 3.3.5

- **IDE**: IntelliJ IDEA 2024.1.4 (Ultimate Edition)
- **OS**: Windows 11
- **MySQL Workbench**: 8.0 CE
- **Docker**: 27.2.0
- **Apache JMeter**: 5.6

<br>

### 🔧 사전 요구사항

1. 필수 소프트웨어
   - **Java**: 17 이상
   - **Gradle**: 8.10.2
   - **MySQL**: 8.0 이상
   - **Docker**: 27.2.0
   - **Docker Compose**: 2.20.2 (선택)
   - **MySQL Workbench**: 8.0 CE (선택)

2. 환경 변수 설정
   - `DATABASE_URL`: `jdbc:mysql://localhost:3306/mydb`
   - `JWT_SECRET`: `your-secret-key`
   - `SPRING_PROFILES_ACTIVE`: `dev`
   - `REDIS_HOST`: `localhost` (선택)

3. 네트워크 요구사항
   - MySQL 기본 포트: `3306`
   - Spring Boot 기본 포트: `8080`
   - Docker 컨테이너 포트 매핑: `127.0.0.1:8080->8080`

4. 권장 환경
   - 운영 체제: Windows 11 / Ubuntu 22.04
   - 최소 메모리: 8GB
   - CPU: 4코어 이상
   - 디스크 여유 공간: 100GB 이상

5. 데이터베이스 설정
   - 스키마 파일: `/db/schema.sql`
   - 초기 데이터 파일: `/db/data.sql`

6. 테스트 환경
   - Apache JMeter: 부하 테스트 설정 파일 (`/test/jmeter-config.jmx`)
   - Postman: Postman 컬렉션 파일 제공 (`/docs/postman-collection.json`)


<br>

## 📌 주요 기능 --> (코드랑 더 상세하게 설명해야하나요?)


1. **회원 서비스**  
    - **회원 가입**  
        - 소셜 로그인을 통한 회원 가입 기능 제공   
        - 사용자 계정과 판매자 계정의 권한 구분  

    - **로그인 및 로그아웃**  
        - JWT 토큰을 활용한 로그인 기능 구현  
        - 로그아웃 기능 제공  



2. **스트리밍 서비스** 
    - **동영상 재생**  
        - 동영상을 재생하는 API를 개발 
        - 동영상 재생 시 조회수가 증가  
        - 재생 중단 시 현재까지 재생한 시점 저장  

    - **광고 시청**  
        - 각 동영상에는 1개 이상의 광고 영상이 등록
        - 광고 영상이 등록된 시점까지 영상이 재생되면 광고 시청 횟수가 카운트(5분 단위)
       


3. **어뷰징 방지** 
    - 동영상 게시자가 동영상을 시청한 경우 조회수 및 광고 시청 횟수에 카운트되지 않음  
    - 30초 이내 동일한 Source(IP, 인증키)로부터의 접속은 어뷰징으로 간주하여 조회수 및 시청 횟수에 카운트되지 않음 




4. **통계 데이터 조회 및 생성 기능** 
    - 1일, 1주일, 1달 동안 조회수가 높은 동영상 TOP 5  
    - 1일, 1주일, 1달 동안 재생 시간이 긴 동영상 TOP 5  
    - **Spring Batch**를 활용하여 적재된 데이터를 기반으로 통계 데이터를 생성  



5. **정산 데이터 생성 기능** 
    - 동영상별 정산 금액 = 업로드 영상 정산 금액 + 광고 영상 정산 금액  
    - 정산 공식:  
        - 업로드 영상 정산 금액 = 영상별 단가 × 조회 수  
        - 광고 영상 정산 금액 = 광고별 단가 × 광고 조회 수  
    - 광고는 영상 길이에 따라 5분당 1개씩 자동 등록  



6. **정산 데이터 조회 기능** 
    - 1일, 1주일, 1달 동안 총 정산 금액 조회  
    - 영상별로 업로드 정산 금액과 광고 정산 금액 조회  
    - 정산 데이터는 실시간으로 누적되어 반영  

<br>

## 🔥 성능 최적화
- **인덱스(Index) 적용**  
  - 주요 컬럼에 인덱스를 추가하여 조회 성능을 최적화.  

- **Chunk 단위 조정**  
  - 기존 100 → 5000으로 변경하여 대규모 데이터 처리 성능을 개선.  
  - JPA 대신 JDBC를 활용하여 데이터 처리 속도를 향상.  

- **로그 분리 처리**  
  - 배치 작업 로그를 별도로 관리하여 분석 및 디버깅을 용이하게 처리.  

- **파티셔닝 및 멀티스레딩 처리**  
  - 데이터를 파티션별로 분리하고 멀티스레딩으로 병렬 처리하여 작업 효율을 최적화.  

- **100만 개 데이터 처리 시간**  
  - **최적화 전**: **6시간 15분**  
  - **최적화 후**: **3분 30초**  

> ⚡️ **최적화를 통해 데이터 처리 속도가 약 107배 향상되었습니다!**

<br>

## 🔫 트러블 슈팅
- **대용량 데이터 처리 문제**  
  - 대량 요청이 서버에 실시간으로 들어올 경우 **메모리 부족(OutOfMemoryError)** 및 **성능 저하** 문제를 방지하기 위해 **배치 프로그램을 도입**

- **멀티스레드 적용 문제**  
  - **청크 단위에서 멀티스레드 처리**를 적용해도 순차적으로 처리되는 한계가 있어, 데이터를 **파티셔닝**으로 분리하고 각 파티션을 **비동기로 병렬 처리**하여 **성능**과 **확장성**을 개선

- **배치 코드 복잡성 문제**  
  - 주/월 단위 배치를 하나의 코드로 처리하려다 불필요한 복잡도가 발생하여, 배치를 각각 분리함으로써 **코드 간결화**, **관리 용이성**, **확장성**을 개선

- **동시성 문제**  
  - 조회수 증가 시 **비관적 락**으로 데이터 충돌 문제를 방지하고, 멀티스레드 구현 시 **videoId**를 기준으로 파티셔닝하여 충돌 가능성이 낮다고 판단, **낙관적 락**을 적용하여 **성능**과 **안정성**을 확보


기술적 의사결정은 문제를 예방하고 효율적인 시스템을 설계하는 과정이고, 트러블 슈팅은 이미 발생한 문제를 해결하는 대응 과정 위 트러블 슈팅은 적절한가요?

<br>




## 🏗 아키텍쳐
![image](https://github.com/user-attachments/assets/b6f8c531-9f69-4083-8d1d-b26b59112463)

<br>

## 🗂 폴더구조
```
┣ 📁streaming-service
     ┣ 📁batch
        ┣ 📁controller
        ┣ 📁entity
        ┣ 📁repo
        ┣ 📁schedule
        ┣ 📁settlementbatch
     ┣ 📁controller
     ┣ 📁entity
     ┣ 📁jwt
     ┣ 📁repository
     ┣ 📁service
┣ 📁user-service
     ┣ 📁java/com/sparta/userservice
        ┣ 📁config
        ┣ 📁controller
        ┣ 📁dto
        ┣ 📁jwt
        ┣ 📁security
        ┣ 📁service
     ┣ 📁resources
        ┣ 📁static
        ┣ 📁templates
┣ 📁settlement-service
```

<br>

## :bookmark: API 문서
🔗 [Postman API Documentation](https://documenter.getpostman.com/view/30989395/2sAYBPktii)

<br>


## 🛠️ 지원 창구

### 연락 방법
- 이메일: jaeyonglee06@gmail.com
- GitHub Issues: https://github.com/lee411806/settlement-platform/issues

### 문제 보고
- 🐞 버그 신고: [버그 신고 템플릿 바로가기](https://github.com/lee411806/settlement-platform/issues/new?assignees=&labels=&projects=&template=%F0%9F%90%9E-%EB%B2%84%EA%B7%B8-%EC%8B%A0%EA%B3%A0.md&title=%22%5BBUG%5D+%3C%EB%B2%84%EA%B7%B8+%EC%9A%94%EC%95%BD%3E%22)

### 문서 및 가이드
- 공식 문서: 설치 가이드, api 사용법 넣을 예정
- FAQ: 자주 발생한 에러 넣을 예정

### 지원 정책
- 지원 시간: 평일 오전 9시 ~ 오후 6시 (KST)
- 긴급 문의: jaeyonglee06@gmail.com으로 연락해주세요.
