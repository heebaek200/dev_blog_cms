# REST API 설계서

## 적용 여부

이 프로젝트는 REST API를 제공하지 않는다.

- 공개 방문자와 관리자는 Spring MVC Controller가 반환하는 Mustache 화면을 사용한다.
- 조회 요청은 GET, 상태를 변경하는 HTML Form 요청은 POST를 사용한다.
- 성공한 변경 요청은 Post/Redirect/Get 방식으로 화면 URL에 Redirect한다.
- JSON 요청·응답, API 전용 DTO와 `/api` Controller는 만들지 않는다.
- 화면 URL, 입력, 이동과 오류 상태는 [화면 설계서](screen-design.md)를 기준으로 한다.

## 결정 근거

- 프로젝트 목적은 하루 안에 단일 관리자 기술 블로그 CMS를 완성하는 것이다.
- 클라이언트 사이드 애플리케이션이나 외부 API 소비자가 요구사항에 없다.
- 서버 렌더링과 HTML Form만으로 공개 조회와 관리자 관리 흐름을 모두 구현할 수 있다.
- 사용하지 않는 REST 계층을 제외해 구현과 검증 범위를 줄인다.

## MVC Form 경계

| 구분 | 입력 | 성공 결과 | 실패 결과 |
|---|---|---|---|
| 화면 조회 | Path Variable | Mustache 화면 | 404 화면 또는 로그인 Redirect |
| 로그인 | Form Field | 관리자 게시글 목록 Redirect | 로그인 Form에 공통 인증 오류 표시 |
| 생성·수정 | Form DTO | 대상 관리 화면 Redirect | 입력값과 Field Error를 같은 Form에 표시 |
| 삭제·상태 변경 | Path Variable과 POST Form | 대상 관리 화면 Redirect | 업무 오류 Message를 관리 화면에 표시 |

## 적용 제외 항목

REST API가 없으므로 API URL·Method·입출력 DTO·JSON 성공 응답·오류 응답·Pagination·API 권한 응답·직렬화 정책은 모두 적용하지 않는다. 게시글 목록의 Pagination 자체도 요구사항 범위 밖이다.
