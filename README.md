# 개인 기술 블로그 CMS

- [프로젝트 설계 문서 보기](docs/index.md)
- [GitHub Projects 보기](https://github.com/heebaek200/dev_blog_cms/projects)

## 로컬 실행

1. `.env.example`을 참고해 저장소 루트에 Git 추적 대상이 아닌 `.env`를 만든다.
2. `SPRING_PROFILES_ACTIVE=local`과 최초 관리자 환경변수 두 개를 입력한다.
3. Windows에서는 `.\gradlew.bat bootRun`, macOS·Linux에서는 `./gradlew bootRun`을 실행한다.

관리자 정보는 관리자가 없는 최초 실행에서만 사용하며 비밀번호는 BCrypt Hash로 저장한다.

