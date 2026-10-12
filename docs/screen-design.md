# 화면 설계서

## 디자인 방향

- 전체 화면은 콘텐츠와 관리 동작에 집중하는 미니멀 스타일을 사용한다.
- 주요 색상은 파스텔 주황 계열을 사용한다.
- 장식 요소를 줄이고 읽기 쉬운 본문, 명확한 여백과 상태 구분을 우선한다.

### 색상 Palette

| 용도 | 색상 |
|---|---|
| 기본 배경 | `#FFF8F3` |
| 콘텐츠 배경 | `#FFFFFF` |
| Primary Pastel Orange | `#EFA66F` |
| 강조·Button | `#A65324` |
| Primary 연한 배경 | `#FFF0E4` |
| 본문 | `#302824` |
| 보조 글자 | `#75665E` |
| 성공 | `#3F7D5A` |
| 경고 | `#A76524` |
| 오류 | `#B85450` |

파스텔 색상 위에 흰 글자를 직접 사용하지 않는다. Button과 주요 Link에는 충분한 대비를 확보하도록 진한 주황색을 사용한다.

## CSS와 반응형 정책

- Bootstrap 5.3.3을 CDN으로 연결한다.
- 프로젝트 내부 `app.css`에는 글 너비, 본문 Typography, Code Block, 상태 Badge와 색상 변수 등 최소 Custom CSS만 작성한다.
- 공개 화면은 읽기에 집중하는 단일 본문 열을 사용한다.
- 관리자 화면은 Desktop에서 Table을 사용하고 좁은 화면에서는 Card 형태로 전환한다.
- 모바일부터 Desktop까지 지원한다.

## 공통 Layout과 Component

- 모든 화면은 Header, Main과 Footer 순서의 공통 Layout을 사용한다.
- 공개 Header에는 Blog Home과 카테고리·태그 탐색 진입점을 둔다.
- 관리자 Header에는 게시글, 카테고리·태그 관리와 로그아웃 진입점을 둔다.
- Header와 Footer는 Mustache 공통 Partial로 분리한다.
- 성공·경고·오류 Message Box를 공통 Component로 사용한다.

## 와이어프레임 대상

정적 HTML 와이어프레임은 `docs/design/wireframes/`에 작성하고 공통 `wireframe.css`를 사용한다.

- 공개 게시글 목록: `public-post-list.html`
- 공개 게시글 상세: `public-post-detail.html`
- 관리자 로그인: `admin-login.html`
- 관리자 게시글 목록: `admin-post-list.html`
- 게시글 작성·수정 Form: `admin-post-form.html`
- 카테고리·태그 관리: `admin-taxonomy.html`
- 404 오류: `error-404.html`

404 와이어프레임의 Layout을 500 오류 화면에도 공통으로 적용한다.

## 화면과 URL

### 공개 화면

| 화면 | Method | URL | 설명 |
|---|---|---|---|
| 게시글 목록 | GET | `/` | 공개 게시글을 최신순으로 표시한다. |
| 게시글 상세 | GET | `/posts/{postId}` | 공개 게시글의 본문을 표시한다. |
| 카테고리별 목록 | GET | `/categories/{categoryId}` | 선택한 카테고리의 공개 게시글만 표시한다. |
| 태그별 목록 | GET | `/tags/{tagId}` | 선택한 태그의 공개 게시글만 표시한다. |

### 관리자 화면과 동작

| 화면·동작 | Method | URL | 결과 |
|---|---|---|---|
| 로그인 화면 | GET | `/admin/login` | 관리자 로그인 Form을 표시한다. |
| 로그인 | POST | `/admin/login` | 성공하면 게시글 관리로 이동한다. |
| 로그아웃 | POST | `/admin/logout` | Session을 종료하고 공개 목록으로 이동한다. |
| 게시글 관리 | GET | `/admin/posts` | 초안과 공개 게시글을 모두 표시한다. |
| 게시글 작성 화면 | GET | `/admin/posts/new` | 빈 작성 Form을 표시한다. |
| 게시글 생성 | POST | `/admin/posts` | 게시글을 생성하고 관리 목록으로 이동한다. |
| 게시글 수정 화면 | GET | `/admin/posts/{postId}/edit` | 저장된 값으로 수정 Form을 표시한다. |
| 게시글 수정 | POST | `/admin/posts/{postId}/edit` | 게시글을 수정하고 관리 목록으로 이동한다. |
| 게시글 삭제 | POST | `/admin/posts/{postId}/delete` | 게시글을 물리 삭제하고 관리 목록으로 이동한다. |
| 게시글 공개 | POST | `/admin/posts/{postId}/publish` | 상태를 `PUBLISHED`로 변경한다. |
| 게시글 초안 전환 | POST | `/admin/posts/{postId}/draft` | 상태를 `DRAFT`로 변경한다. |
| 분류 관리 | GET | `/admin/taxonomies` | 카테고리와 태그를 한 화면에서 관리한다. |
| 카테고리 생성 | POST | `/admin/categories` | 카테고리를 생성한다. |
| 카테고리 삭제 | POST | `/admin/categories/{categoryId}/delete` | 미사용 카테고리를 삭제한다. |
| 태그 생성 | POST | `/admin/tags` | 태그를 생성한다. |
| 태그 삭제 | POST | `/admin/tags/{tagId}/delete` | 미사용 태그를 삭제한다. |

HTML Form만으로 동작하도록 변경 요청에도 POST를 사용한다. 성공한 변경 요청은 Post/Redirect/Get을 적용한다.

## 화면 명세

| 화면 | 사용자 | 입력 | 표시 정보 | 주요 동작과 이동 | 오류 처리 |
|---|---|---|---|---|---|
| 공개 게시글 목록 | 공개 방문자 | 카테고리 또는 태그 Path ID | 공개 글 제목·작성일·카테고리·태그 | 상세 또는 다른 분류 목록으로 이동 | 없는 분류는 404, 결과가 없으면 빈 상태 표시 |
| 공개 게시글 상세 | 공개 방문자 | 게시글 Path ID | 제목·본문·작성일·카테고리·태그 | 목록 또는 분류 목록으로 이동 | 없는 글과 초안은 404 |
| 관리자 로그인 | 비인증 관리자 | 아이디, 비밀번호 | 로그인 안내와 인증 오류 | 성공 시 게시글 관리로 이동 | 실패 사유를 구분하지 않는 공통 문구 표시 |
| 관리자 게시글 목록 | 관리자 | 게시글별 공개·초안·삭제 Form | 모든 글의 제목·분류·상태·수정일 | 작성·수정·상태 전환·삭제 | 처리 실패는 Message Box에 표시 |
| 게시글 작성·수정 | 관리자 | 제목, 본문, 카테고리 0..1개, 태그 0..5개 | 입력값과 Field Error | 초안 저장 또는 공개 후 목록으로 이동 | 검증 오류 시 입력값을 유지해 같은 Form 표시 |
| 카테고리·태그 관리 | 관리자 | 새 분류 이름과 삭제 Form | 분류 이름과 사용 글 수 | 생성·삭제 후 같은 화면으로 이동 | 중복 이름과 사용 중 삭제 오류를 Message Box에 표시 |
| 오류 | 모든 사용자 | HTTP 오류 상태 | 안전한 안내 문구 | 공개 게시글 목록으로 이동 | 내부 Exception과 상세 정보는 표시하지 않음 |

## 화면별 상태

| 화면 | 기본 상태 외에 반드시 표현할 상태 |
|---|---|
| 공개 게시글 목록 | 게시글 없음, 카테고리·태그 필터 결과 없음 |
| 공개 게시글 상세 | 존재하지 않거나 비공개인 게시글의 404 |
| 관리자 로그인 | 자격 증명 오류 |
| 관리자 게시글 목록 | 게시글 없음, 처리 성공 Message |
| 게시글 작성·수정 | 입력값 검증 오류와 입력값 유지 |
| 카테고리·태그 관리 | 이름 중복 오류, 사용 중인 분류 삭제 실패 |
| 오류 | 요청 자원 없음 404, 서버 오류 500 |

- 인증되지 않은 관리 요청은 로그인 화면으로 이동한다.
- 입력값 오류는 해당 Form에 Field Error와 함께 표시한다.
- 중복 또는 사용 중 삭제 같은 업무 오류는 해당 관리 화면의 Message Box로 표시한다.
- 별도의 400 또는 409 오류 화면은 만들지 않는다.

## 확정된 화면 범위 제약

- 검색 기능을 구현하지 않으며 제목 검색 입력창과 검색 버튼을 표시하지 않는다.
- 페이지네이션을 구현하지 않으며 페이지 번호, 이전·다음과 더 보기 UI를 표시하지 않는다.
- 공개 회원가입, 다중 작성자, 댓글, 좋아요와 이미지·파일 업로드 기능을 암시하는 Form, Button과 Link를 표시하지 않는다.
- 관리자 게시글 작성·수정 화면에는 초안 저장과 공개 동작을 구분해 제공한다.
- 관리자 화면에서는 초안과 공개 상태를 구분해 표시한다.
- 게시글의 카테고리는 미지정 또는 하나만 선택할 수 있고 태그는 최대 5개까지 선택할 수 있다.
- 별도 요약 필드를 두지 않으며 공개 목록에도 본문 요약을 표시하지 않는다.

## 작성 결과

- [공개 게시글 목록](https://heebaek200.github.io/dev_blog_cms/design/wireframes/public-post-list.html)
- [공개 게시글 상세](https://heebaek200.github.io/dev_blog_cms/design/wireframes/public-post-detail.html)
- [관리자 로그인](https://heebaek200.github.io/dev_blog_cms/design/wireframes/admin-login.html)
- [관리자 게시글 목록](https://heebaek200.github.io/dev_blog_cms/design/wireframes/admin-post-list.html)
- [게시글 작성·수정 Form](https://heebaek200.github.io/dev_blog_cms/design/wireframes/admin-post-form.html)
- [카테고리·태그 관리](https://heebaek200.github.io/dev_blog_cms/design/wireframes/admin-taxonomy.html)
- [404 오류](https://heebaek200.github.io/dev_blog_cms/design/wireframes/error-404.html)
