# 코드 컨벤션

개인 숙련자 프로젝트를 기준으로, 선택이 갈리거나 위반 시 실제 결함이 생기는 규칙만 기록한다. Java 기본 문법과 일반적인 명명 규칙은 반복하지 않는다.

## 패키지와 의존 방향

- `administrator`, `post`, `category`, `tag` 도메인 아래에 해당 Controller, Service, Repository와 DTO를 둔다.
- 계층별 최상위 패키지는 만들지 않고, 공통 기반만 `_core`에 둔다.
- Controller는 Service만 호출하고 Repository를 직접 호출하지 않는다.
- Service는 유스케이스에 필요한 여러 Repository를 호출할 수 있지만 다른 Service는 호출하지 않는다.
- Repository는 화면 Model이나 Form DTO를 반환하지 않는다.

## 트랜잭션

- Service의 public 유스케이스 메서드를 트랜잭션 경계로 사용한다.
- 조회에는 `@Transactional(readOnly = true)`, 변경에는 `@Transactional`을 사용한다.

## 프로젝트 명명

- 공개 게시글 Controller는 `PostController`를 사용한다.
- 관리자 Controller는 `AdminAuthController`, `AdminPostController`, `AdminTaxonomyController`를 사용한다.
- Form DTO는 `{Domain}Request`의 `Create`, `Update`, `Login` 등 중첩 타입으로 작성한다.
- 화면 조회 DTO는 `{Domain}Response`의 `ListItem`, `Detail`, `EditForm` 등 중첩 타입으로 작성한다.
- Request와 Response 타입의 용도를 서로 바꾸어 사용하지 않는다.
- REST API가 없으므로 `ApiController` 접미사는 사용하지 않는다.

## `_core`

- `_core/config`: JPA Auditing 등 전역 Spring 설정
- `_core/entity`: `BaseTimeEntity` 등 공통 Entity 기반
- `_core/exception`: 공통 예외와 Exception Handler
- `_core/interceptor`: 관리자 인증 등 요청 공통 처리
- `_core/util`: 둘 이상의 도메인이 실제로 공유하는 무상태 기능이 있을 때만 생성
- 업무 Entity, DTO, Controller, Service와 Repository는 `_core`에 두지 않는다.

## Javadoc과 로깅

- 주요 Java 클래스와 메서드에는 목적과 처리 의도를 기록하고 필요한 `@param`, `@return`, `@throws`를 작성한다.
- Lombok 생성 코드, 단순 Getter·Setter, 자명한 Override와 Test Method에는 Javadoc을 작성하지 않는다.
- SLF4J와 `@Slf4j`를 사용하며 시스템 실패는 `ERROR`, 인증 거부·업무 충돌은 `WARN`, 관리자 초기화·주요 변경은 `INFO`, 개발 진단은 `DEBUG`로 기록한다.
- 비밀번호, Session ID와 개인정보를 기록하지 않는다.
- `System.out`, `System.err`, 직접 Stack Trace 출력과 Pull Request에 남은 임시 `DEBUG` Log를 금지한다.
- 별도의 애플리케이션 Log Level 설정은 추가하지 않는다.

## 검증

- Form DTO는 Jakarta Bean Validation과 `@Valid`, `BindingResult`를 사용한다.
- 게시글 제목은 Trim 후 필수·최대 200자, 본문은 Trim 후 필수로 검증한다.
- 카테고리·태그 이름은 Trim 후 필수·최대 50자로 검증한다.
- 태그 ID는 `Set<Long>`으로 받고 최대 5개로 제한하며 카테고리 ID는 선택값으로 받는다.
- 로그인 아이디와 비밀번호는 필수로 검증한다.
- Form 오류는 Service 호출 전에 입력값과 Field Error를 같은 화면에 반환한다.
- 상태 전환처럼 Entity 자신만의 규칙은 Entity에서, Repository 조회가 필요한 중복·사용 여부 규칙은 Service에서 검증한다.

## 관리자 인증과 Session

- `HandlerInterceptor`를 `/admin/**`에 적용하고 `/admin/login`, `/css/**`, `/error`, `/favicon.ico`를 제외한다.
- 미인증 요청은 `Exception401`을 발생시켜 `/admin/login`으로 Redirect한다.
- Interceptor에서 Form DTO를 검증하지 않는다.
- Session Key는 `Define.SESSION_ADMIN`과 값 `sessionAdmin`을 사용한다.
- Session에는 Entity 대신 관리자 ID와 아이디만 가진 `AdministratorSession`을 저장한다.
- 로그아웃은 Session 전체를 무효화하고, 조회 시 불필요한 Session을 생성하지 않는다.

## 예외

- 참조 프로젝트와 같이 RuntimeException과 `@ControllerAdvice`를 사용한다.
- `Exception401`, `Exception404`, `Exception500`은 각각 미인증, 미존재·비공개 자원, 명시적 내부 실패에 사용한다.
- `DuplicateTaxonomyException`, `TaxonomyInUseException`, `InvalidPostStateException`은 해당 업무 충돌에 사용한다.
- 401은 로그인 Redirect, 404·500은 `error/404`, `error/500` 화면, 업무 충돌은 해당 관리 화면 Redirect와 Flash Message로 처리한다.
- 미처리 RuntimeException은 상세 Log를 남기되 사용자에게 내부 메시지를 노출하지 않고 500으로 처리한다.
- 존재하지 않는 Route는 Spring Boot `/error` Mapping과 `templates/error/404.mustache`로 처리한다.
- JavaScript `alert` 문자열을 오류 응답으로 반환하지 않는다.

## 테스트

- Repository는 `@DataJpaTest`, 애플리케이션 시작은 Context Load Test로 검증한다.
- 핵심 Service 업무 규칙과 관리자 Interceptor는 별도 Test로 검증한다.
- Test Method는 `메서드_조건_기대결과` 형식과 사용자 관점의 `@DisplayName`을 사용한다.
- Test에서도 직접 콘솔 출력을 사용하지 않는다.

## JPA

- 단순 CRUD와 파생 조회는 `JpaRepository`를 사용한다.
- 관리 상태의 Entity 수정은 변경 감지를 사용하며 수정 후 `save()`를 다시 호출하지 않는다.
- 모든 연관관계는 `LAZY`로 설정한다.
- 게시글 목록·상세의 카테고리와 태그는 JPQL Fetch Join 또는 `@EntityGraph`로 함께 조회한다.
- 게시글과 태그는 `@ManyToMany` 대신 `PostTag` 연결 Entity로 Mapping한다.
- `Post → PostTag`에만 Cascade와 `orphanRemoval`을 적용하고 Category·Tag 방향 Cascade는 사용하지 않는다.
- 조회는 JpaRepository Method, JPQL `@Query`, Custom Repository 순서로 해결한다.
- 현재 범위에서는 EntityManager를 직접 사용하지 않고 Native SQL과 MyBatis를 도입하지 않는다.
