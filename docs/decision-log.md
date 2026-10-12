# 설계 결정 기록

프로젝트의 여러 영역에 영향을 주거나 기존 방향을 변경한 주요 결정만 기록합니다.

## 2026-10-12 하루 완성 과제

- 결정: 하루 안에 스프링 부트 프로젝트를 완성해 보는 과제로 진행한다.
- 상태: confirmed
- 근거: 사용자가 프로젝트 목적과 시간 제약을 명시했다.
- 영향 문서: requirements.md, common-foundation.yaml

## 2026-10-12 단일 관리자 기술 블로그 CMS

- 결정: 공개 방문자는 글을 읽고 단일 관리자만 게시글, 카테고리와 태그를 관리한다. 기존 Spring MVC, Mustache, JPA와 Session 구조를 유지하면서 참조 구현의 결함을 개선한다.
- 상태: confirmed
- 근거: 하루 완성 제약 안에서 개인 기술 블로그 CMS의 운영 모델과 완성도를 모두 만족하기 위해 사용자가 추천안을 선택했다.
- 영향 문서: requirements.md, business-rules.md, common-foundation.yaml

## 2026-10-12 하루 구현 범위 고정

- 결정: 검색과 페이지네이션을 포함한 비필수 기능을 구현하지 않으며, 미구현 기능을 암시하는 UI도 제공하지 않는다. Build, Test, 실행과 핵심 사용자 흐름을 완료 기준으로 사용한다.
- 상태: confirmed
- 근거: 하루 안에 실제로 동작하는 필수 흐름의 완성도를 우선한다.
- 영향 문서: requirements.md, screen-design.md, common-foundation.yaml

## 2026-10-12 요구사항 문서의 역할

- 결정: requirements.md에는 가상 클라이언트 A씨가 요청한 제품 요구사항과 인수 기준만 기록한다. 참조 분석은 reference-analysis.md, 설계 결정은 decision-log.md, 구현 계약은 common-foundation.yaml에 분리한다.
- 상태: confirmed
- 근거: 요구사항과 아말라이트의 내부 분석·결정 과정을 혼합하지 않기 위해 사용자가 문서 책임을 명확히 했다.
- 영향 문서: requirements.md, reference-analysis.md, decision-log.md, common-foundation.yaml

## 2026-10-12 관리자와 콘텐츠 생명주기

- 결정: 환경변수로 최초 관리자 계정 하나를 만들고, 게시글은 초안과 공개 상태만 사용한다. 카테고리는 게시글당 0개 또는 1개, 태그는 최대 5개로 제한한다.
- 상태: confirmed
- 근거: 하루 구현 범위 안에서 관리자 초기화와 콘텐츠 공개 흐름을 명확히 유지한다.
- 영향 문서: requirements.md, business-rules.md, database-design.md, screen-design.md, common-foundation.yaml

## 2026-10-12 데이터베이스 전역 정책

- 결정: local·Test는 H2, dev는 환경변수 기반 MySQL을 사용한다. 단수형 snake_case 테이블, 테이블명 기반 PK·FK, LocalDateTime과 Asia/Seoul 기준, 문자열 Enum, 생성·수정 시각 공통 컬럼을 사용한다.
- 상태: confirmed
- 근거: 시작 도구의 Profile 구성을 유지하면서 H2와 MySQL 사이에 일관된 명명과 데이터 표현을 적용한다.
- 영향 문서: database-design.md, common-foundation.yaml

## 2026-10-12 미니멀 파스텔 주황 화면 체계

- 결정: 미니멀 스타일, 파스텔 주황 Palette, Bootstrap 5.3.3 CDN과 최소 app.css를 사용한다. 모바일·Desktop 반응형과 Mustache 공통 Header·Footer·Message Box를 적용하고 주요 화면의 정적 HTML 와이어프레임을 작성한다.
- 상태: confirmed
- 근거: 하루 구현 범위 안에서 일관된 시각 품질과 읽기 쉬운 기술 블로그 화면을 확보한다.
- 영향 문서: screen-design.md, common-foundation.yaml

## 2026-10-12 화면 URL과 상태 처리

- 결정: 공개 URL과 `/admin` 관리 URL을 분리하고 HTML Form 변경 요청에는 POST를 사용한다. 목록·Form·분류 관리의 빈 상태와 오류 상태를 화면 안에서 처리하며, 독립 오류 화면은 404와 500만 제공한다.
- 상태: confirmed
- 근거: 서버 렌더링 MVC 구조에서 하루 안에 구현 가능한 명시적 사용자 흐름과 오류 처리를 유지한다.
- 영향 문서: screen-design.md, common-foundation.yaml

## 2026-10-12 REST API 미사용

- 결정: JSON REST API와 `/api` Controller를 만들지 않고 Spring MVC, Mustache와 HTML Form 요청만 사용한다.
- 상태: confirmed
- 근거: 외부 API 소비자가 없는 서버 렌더링 프로젝트이며 하루 완성 범위에서 중복된 전달 계층을 제외한다.
- 영향 문서: api-design.md, screen-design.md, common-foundation.yaml

## 2026-10-12 개인 숙련자용 컨벤션과 도메인 중심 패키지

- 결정: 컨벤션 문서는 설명과 예제 없이 판별 가능한 규칙만 나열한다. 참조 프로젝트와 같이 업무 도메인별 패키지를 사용하고 공유 기반 코드만 `_core`에 둔다.
- 상태: confirmed
- 근거: 숙련자가 단독으로 구현하는 프로젝트이므로 교육용 설명을 생략하고 참조 구현과 패키지 탐색 방식을 맞춘다.
- 영향 문서: convention.md, common-foundation.yaml

## 2026-10-12 계층 의존과 트랜잭션 경계

- 결정: `Controller → Service → Repository` 방향을 사용하고 Controller의 Repository 직접 호출과 Service 간 직접 호출을 금지한다. Service는 필요한 여러 Repository를 사용하며 public 유스케이스 메서드가 트랜잭션 경계가 된다.
- 상태: confirmed
- 근거: 단일 프로젝트에서도 계층 책임과 트랜잭션 범위를 명확히 하고 순환 의존을 방지한다.
- 영향 문서: convention.md, common-foundation.yaml

## 2026-10-12 Java와 MVC 명명

- 결정: Entity·Repository·Service는 역할 접미사를 사용하고 공개 및 관리자 MVC Controller를 구분한다. Form 요청과 화면 조회 DTO는 도메인별 Request·Response Class의 중첩 타입으로 구성하며 API Controller 명명은 사용하지 않는다.
- 상태: confirmed
- 근거: 도메인 중심 Package 안에서 역할과 화면 경계를 이름만으로 식별한다.
- 영향 문서: convention.md, common-foundation.yaml

## 2026-10-12 공통 패키지와 코드 기록 규칙

- 결정: `_core`에는 설정, 공통 Entity 기반, 예외와 Interceptor만 두고 필요가 확인되기 전에는 util을 만들지 않는다. 주요 Java 코드에는 전역 기준의 Javadoc을 작성하고 SLF4J Level과 민감정보 금지 규칙을 적용한다.
- 상태: confirmed
- 근거: 공통 Package의 비대화를 막으면서 구현 의도와 운영상 필요한 사건을 일관되게 남긴다.
- 영향 문서: convention.md, common-foundation.yaml

## 2026-10-12 게시글 요약 필드 제외

- 결정: 별도 게시글 요약 필드를 두지 않고 공개 목록에도 본문 요약을 표시하지 않는다.
- 상태: not_applicable
- 근거: 클라이언트 요구사항과 데이터베이스 설계에 없는 기능을 제거해 하루 구현 범위를 유지하고 화면·데이터 모델을 일치시킨다.
- 영향 문서: screen-design.md, common-foundation.yaml, docs/design/wireframes

## 2026-10-12 Form 검증과 관리자 요청 인증

- 결정: Form 형식은 Jakarta Bean Validation과 BindingResult로 처리하고, 여러 Entity가 관련된 업무 규칙은 Service에서 검증한다. `/admin/**`의 Session 인증은 HandlerInterceptor로 공통화하며 로그인·정적 리소스·오류 경로는 제외한다.
- 상태: confirmed
- 근거: 인증과 입력·도메인 검증의 책임을 분리하고 관리 경로의 누락 없는 접근 통제를 적용한다.
- 영향 문서: convention.md, common-foundation.yaml

## 2026-10-12 후속 컨벤션의 참조 프로젝트 기준선

- 결정: 예외, Session과 Test도 참조 프로젝트의 구조를 기본으로 사용하되 확정된 보안·화면·로깅 규칙에 어긋나는 구현은 개선한다.
- 상태: confirmed
- 근거: 수강 과정에서 사용한 구현 흐름을 유지하면서 이미 식별한 결함을 새 프로젝트에 반복하지 않는다.
- 영향 문서: reference-analysis.md, convention.md, common-foundation.yaml

## 2026-10-12 예외·Session·Test 규칙

- 결정: 참조 프로젝트의 RuntimeException, ControllerAdvice, Session 상수와 DataJpaTest 구조를 유지한다. 업무 충돌은 의미 있는 예외로 분리하고, Session에는 Entity 대신 최소 관리자 정보를 저장하며 핵심 Service와 Interceptor Test를 보강한다.
- 상태: confirmed
- 근거: 수강 예제와의 구조적 연속성을 지키면서 보안, 오류 화면과 핵심 업무 규칙의 검증 결함을 개선한다.
- 영향 문서: convention.md, common-foundation.yaml

## 2026-10-12 컨벤션 문서의 규칙 선별

- 결정: 숙련자에게 자명한 Java 기본 문법과 일반론은 컨벤션 문서에서 제외하고, 프로젝트 선택사항과 결함 방지 규칙만 기록한다.
- 상태: confirmed
- 근거: 개인 프로젝트에서 문서의 신호 대비 잡음을 줄이고 실제 구현 판단에 필요한 규칙을 빠르게 찾는다.
- 영향 문서: convention.md, common-foundation.yaml

## 2026-10-12 JPA 영속성과 조회 전략

- 결정: JpaRepository와 변경 감지를 사용하고 모든 연관관계는 지연 로딩한다. 게시글·태그는 명시적 PostTag Entity로 Mapping하며 목록·상세의 N+1은 JPQL Fetch Join 또는 EntityGraph로 방지한다. EntityManager 직접 사용, Native SQL과 MyBatis는 현재 범위에서 제외한다.
- 상태: confirmed
- 근거: 참조 프로젝트의 JPA 흐름을 유지하면서 관계 Mapping과 조회 성능 규칙을 명확히 한다.
- 영향 문서: convention.md, database-design.md, common-foundation.yaml

## 2026-10-12 실행 환경과 최초 관리자 초기화

- 결정: Java 21과 Spring Boot 4.1.1을 유지하고 local·dev·test Profile을 각각 H2 create, MySQL update, H2 create-drop으로 구성한다. 관리자가 없을 때 환경변수의 자격 증명을 BCrypt Hash해 최초 계정을 만들며 필수 값이 없으면 시작을 실패 처리한다.
- 상태: confirmed
- 근거: 시작 도구의 환경 구성을 유지하면서 실제 비밀정보 없이 단일 관리자 초기화를 재현 가능하게 만든다.
- 영향 문서: database-design.md, common-foundation.yaml

## 2026-10-12 스피어 공통 기반 범위

- 결정: 환경·DB·최초 관리자·JPA Auditing·Validation·예외·인증 Guard·공통 화면과 CSS를 스피어에 포함한다. 실제 로그인과 게시글·분류 업무 기능은 제외하며 검증은 Test, local 시작, 핵심 공통 흐름과 비밀정보 확인으로 제한한다.
- 상태: confirmed
- 근거: 업무 기능을 선행 구현하지 않으면서 이후 기능 개발이 바로 사용할 수 있는 공통 기반만 완성한다.
- 영향 문서: common-foundation.yaml
