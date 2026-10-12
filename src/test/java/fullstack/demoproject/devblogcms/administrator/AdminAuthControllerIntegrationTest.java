package fullstack.demoproject.devblogcms.administrator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import fullstack.demoproject.devblogcms._core.config.Define;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminAuthControllerIntegrationTest {

	private static final String INVALID_CREDENTIALS_MESSAGE = "아이디 또는 비밀번호를 확인해 주세요.";

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("관리자 로그인 화면은 Session 없이 조회할 수 있다")
	void loginForm_withoutSession_returnsLoginView() throws Exception {
		mockMvc.perform(get("/admin/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/login"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("관리자 로그인")));
	}

	@Test
	@DisplayName("올바른 관리자 정보로 로그인하면 Session을 만들고 게시글 관리로 이동한다")
	void login_validCredentials_storesSessionAndRedirects() throws Exception {
		MvcResult result = mockMvc.perform(post("/admin/login")
						.param("username", "test-admin")
						.param("password", "test-password-only"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/posts"))
				.andReturn();

		MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
		assertThat(session).isNotNull();
		assertThat(session.getAttribute(Define.SESSION_ADMIN))
				.isInstanceOf(AdministratorSession.class)
				.extracting("username")
				.isEqualTo("test-admin");
	}

	@Test
	@DisplayName("아이디가 없거나 비밀번호가 틀리면 같은 인증 오류를 표시하고 Session을 만들지 않는다")
	void login_invalidCredentials_returnsSameErrorWithoutSession() throws Exception {
		MvcResult unknownUsernameResult = mockMvc.perform(post("/admin/login")
						.param("username", "unknown")
						.param("password", "wrong-password"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/login"))
				.andExpect(model().attribute("errorMessage", INVALID_CREDENTIALS_MESSAGE))
				.andReturn();

		MvcResult wrongPasswordResult = mockMvc.perform(post("/admin/login")
						.param("username", "test-admin")
						.param("password", "wrong-password"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/login"))
				.andExpect(model().attribute("errorMessage", INVALID_CREDENTIALS_MESSAGE))
				.andReturn();

		assertThat(unknownUsernameResult.getRequest().getSession(false)).isNull();
		assertThat(wrongPasswordResult.getRequest().getSession(false)).isNull();
	}

	@Test
	@DisplayName("필수 로그인 입력이 비어 있으면 Field Error와 기존 아이디를 표시한다")
	void login_blankFields_returnsValidationErrors() throws Exception {
		mockMvc.perform(post("/admin/login")
						.param("username", "")
						.param("password", ""))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/login"))
				.andExpect(model().attribute("usernameError", "아이디를 입력해 주세요."))
				.andExpect(model().attribute("passwordError", "비밀번호를 입력해 주세요."));
	}

	@Test
	@DisplayName("로그아웃하면 기존 Session 전체를 무효화하고 공개 목록으로 이동한다")
	void logout_authenticatedSession_invalidatesSessionAndRedirects() throws Exception {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(Define.SESSION_ADMIN, new AdministratorSession(1L, "test-admin"));

		mockMvc.perform(post("/admin/logout").session(session))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));

		assertThat(session.isInvalid()).isTrue();
	}
}
