package fullstack.demoproject.devblogcms._core.interceptor;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import fullstack.demoproject.devblogcms._core.config.Define;
import fullstack.demoproject.devblogcms._core.exception.Exception401;
import fullstack.demoproject.devblogcms.administrator.AdministratorSession;

class AdminAuthenticationInterceptorTest {

	private final AdminAuthenticationInterceptor interceptor = new AdminAuthenticationInterceptor();

	@Test
	@DisplayName("관리자 Session이 없으면 보호된 요청을 거부한다")
	void preHandle_missingAdministratorSession_throwsUnauthorizedException() {
		MockHttpServletRequest request = new MockHttpServletRequest();

		assertThatThrownBy(() -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()))
				.isInstanceOf(Exception401.class);
	}

	@Test
	@DisplayName("관리자 Session이 있으면 보호된 요청을 통과시킨다")
	void preHandle_authenticatedAdministrator_allowsRequest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.getSession().setAttribute(Define.SESSION_ADMIN, new AdministratorSession(1L, "admin"));

		assertThatCode(() -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()))
				.doesNotThrowAnyException();
	}
}
