# 참조 프로젝트 분석

이 문서는 수업에서 작성한 예제 프로젝트의 구현 수준을 확인하고 현재 프로젝트의 개선 방향을 비교하기 위한 내부 설계 자료다. 클라이언트 요구사항과 확정된 구현 계약은 각각 `requirements.md`와 `common-foundation.yaml`을 기준으로 한다.

## 분석 기준

- 저장소: `https://github.com/heebaek200/spring_blog`
- Branch: `main`
- Commit: `3b5e0b3775029e4a4954d0fc2fec07f7c28ee390`
- 분석 시각: 2026-10-12T10:06:32+09:00
- 분석 목적: 수업에서 사용한 기술과 구현 수준을 확인하고 하루 안에 가능한 개선 범위를 정한다.
- 접근 결과: 성공. 소스와 문서만 읽었으며 Script, Build와 애플리케이션은 실행하지 않았다.

## 관찰한 구현

- Java 21, Spring Boot 4.1.1과 Gradle을 사용한다.
- Spring MVC와 Mustache로 서버에서 HTML을 렌더링하고 Bootstrap 5.3.3을 CDN으로 제공한다.
- `board`, `user` 도메인 Package와 `_core` 공통 Package를 둔 도메인 중심 구조다.
- Controller → Service → `JpaRepository` 흐름과 Service의 Transaction 경계를 사용한다.
- JPA Entity는 사용자와 게시글이며, 게시글과 작성자를 지연 로딩 관계로 연결하고 목록·상세 조회에서는 Fetch Join을 사용한다.
- Session 로그인과 `HandlerInterceptor`로 인증, IP 차단과 사용자 차단을 처리한다.
- HTTP 상태별 사용자 정의 Exception, 공통 `ControllerAdvice`와 Mustache 오류 화면을 사용한다.
- 회원가입, 로그인, 로그아웃, 회원정보 수정과 게시글 CRUD가 구현돼 있다. 댓글은 상세 화면의 예시 UI만 있고 실제 기능은 없다.
- Repository 중심의 JPA Test와 Context Test가 있으며 Service, Controller와 핵심 사용자 흐름 Test는 부족하다.

## 개선 필요 사항

- 비밀번호를 평문으로 저장하고 직접 비교하므로 안전한 단방향 Hash 정책이 필요하다.
- DTO의 직접 작성한 `validate()`가 Controller와 Entity에 중복 호출되므로 검증 책임을 정리할 필요가 있다.
- 로그아웃이 상태를 바꾸는 GET 요청이며, 인증·인가 실패와 오류 화면 처리 방식이 일부 일관되지 않다.
- IP와 사용자 차단 정책이 코드에 고정돼 있고 모든 요청에 일괄 적용된다.
- 이름 오타, 남은 TODO·주석 코드, 실제 기능이 없는 댓글 Form과 일부 과도한 Log가 남아 있다.
- 회원·게시글 핵심 흐름과 권한 규칙을 자동 검증하는 Test가 부족하다.

## 구현 방식 비교

### 참조 호환 방식

참조 프로젝트의 기술과 구조를 거의 유지한다. 학습 비용과 구현 시간은 가장 작지만 기존 결함이 반복될 가능성이 높다.

### 개선 방식

Spring MVC, Mustache, JPA, Session과 도메인 중심 구조는 유지하면서 핵심 기능 범위를 통제하고 보안, 검증, 예외와 핵심 Test의 명확한 결함을 개선한다. 하루 제약과 완성도 목표의 균형이 가장 좋다.

후속 코드 컨벤션도 참조 프로젝트를 기준선으로 사용한다. HTTP 상태별 RuntimeException, `@ControllerAdvice`, `HandlerInterceptor`, Session 상수와 `@DataJpaTest` 구조는 유지한다. 평문 비밀번호 비교, `System.out` 출력, JavaScript 문자열 오류 응답과 자명한 Interceptor Override는 가져오지 않는다.

### 현대적 대안

Spring Security, 표준 Bean Validation, Database Migration과 더 넓은 통합 Test를 도입한다. 장기 유지보수에는 유리하지만 추가 학습과 설정 비용 때문에 하루 완성 위험이 크다.

## 선택 결과

현재 프로젝트는 **개선 방식**을 사용한다. 참조 저장소의 관찰 결과는 자동으로 구현 기준이 되지 않으며, 아말라이트에서 별도로 확정한 결정만 실행 계약에 반영한다.
