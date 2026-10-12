package fullstack.demoproject.devblogcms._core.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class CommonTemplateContractTest {

	@Test
	@DisplayName("404와 500 화면은 같은 Header와 Footer Component를 사용한다")
	void errorTemplates_renderSharedLayoutPartials() throws IOException {
		String notFoundTemplate = readResource("templates/error/404.mustache");
		String internalErrorTemplate = readResource("templates/error/500.mustache");

		assertThat(notFoundTemplate).contains("{{>layout/header}}", "{{>layout/footer}}");
		assertThat(internalErrorTemplate).contains("{{>layout/header}}", "{{>layout/footer}}");
	}

	@Test
	@DisplayName("공통 Header는 Bootstrap 5.3.3 CDN과 app.css를 연결한다")
	void headerTemplate_loadsConfirmedStyles() throws IOException {
		String headerTemplate = readResource("templates/layout/header.mustache");

		assertThat(headerTemplate).contains("bootstrap@5.3.3", "/css/app.css");
	}

	@Test
	@DisplayName("관리자 로그인 화면은 공통 Layout과 Message Component를 사용한다")
	void loginTemplate_rendersSharedComponents() throws IOException {
		String loginTemplate = readResource("templates/admin/login.mustache");

		assertThat(loginTemplate).contains(
				"{{>layout/header}}",
				"{{>components/messages}}",
				"{{>layout/footer}}"
		);
	}

	@Test
	@DisplayName("분류 관리 화면은 공통 Layout과 Message Component를 사용한다")
	void taxonomyTemplate_rendersSharedComponents() throws IOException {
		String taxonomyTemplate = readResource("templates/admin/taxonomies.mustache");

		assertThat(taxonomyTemplate).contains(
				"{{>layout/header}}",
				"{{>components/messages}}",
				"{{>layout/footer}}"
		);
	}

	@Test
	@DisplayName("게시글 관리 화면은 공통 Layout과 Message Component를 사용한다")
	void postManagementTemplates_renderSharedComponents() throws IOException {
		String listTemplate = readResource("templates/admin/posts.mustache");
		String formTemplate = readResource("templates/admin/post-form.mustache");

		assertThat(listTemplate).contains(
				"{{>layout/header}}",
				"{{>components/messages}}",
				"{{>layout/footer}}"
		);
		assertThat(formTemplate).contains(
				"{{>layout/header}}",
				"{{>components/messages}}",
				"{{>layout/footer}}"
		);
		assertThat(listTemplate).contains("return confirm('게시글을 삭제하시겠습니까?");
		assertThat(listTemplate).contains("admin-post-title-text", "title=\"{{title}}\"");
		assertThat(formTemplate).contains("return confirm('게시글을 삭제하시겠습니까?");
	}

	@Test
	@DisplayName("공통 Message Component는 성공·경고·오류 상태를 모두 지원한다")
	void messageComponent_supportsAllConfirmedStates() throws IOException {
		String messageComponent = readResource("templates/components/messages.mustache");

		assertThat(messageComponent).contains("successMessage", "warningMessage", "errorMessage");
	}

	private String readResource(String path) throws IOException {
		return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
	}
}
