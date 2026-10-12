# 개인 기술 블로그 CMS

Spring Boot 학습 예제를 바탕으로, 하루 안에 설계부터 구현과 검증까지 확장해 완성한 단일 관리자 기술 블로그 CMS입니다.
기능 범위를 작게 유지하면서 관리자 인증, 게시글과 분류 관리, 공개 조회까지 서버 렌더링 방식으로 구현했습니다.

## 주요 기능

- 최초 관리자 생성과 Session 기반 로그인·로그아웃
- 카테고리·태그 생성, 중복 방지와 사용 중 삭제 차단
- 게시글 작성·수정·삭제 및 초안·공개 상태 전환
- 공개 게시글 목록·상세와 카테고리·태그 필터
- 공통 404·500 화면과 관리자 요청 접근 제어

## 기술 스택

- Java 21, Spring Boot 4.1.1
- Spring MVC, Mustache, Bootstrap 5.3.3
- Spring Data JPA, Hibernate
- H2(local·test), MySQL(dev)
- Gradle, JUnit 5, AssertJ, MockMvc

## 문서

- [프로젝트 설계 문서 보기](docs/index.md)
- [GitHub Projects 보기](https://github.com/heebaek200/dev_blog_cms/projects)

## 로컬 실행

1. `.env.example`을 참고해 저장소 루트에 Git 추적 대상이 아닌 `.env`를 만든다.
2. `SPRING_PROFILES_ACTIVE=local`과 최초 관리자 환경변수 두 개를 입력한다.
3. Windows에서는 `.\gradlew.bat bootRun`, macOS·Linux에서는 `./gradlew bootRun`을 실행한다.

관리자 정보는 관리자가 없는 최초 실행에서만 사용하며 비밀번호는 BCrypt Hash로 저장한다.

## 검증

```shell
./gradlew clean build
```

Windows에서는 `./gradlew` 대신 `.\gradlew.bat`을 사용합니다.

