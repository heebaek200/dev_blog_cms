package fullstack.demoproject.devblogcms._core.interceptor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminRequestGuardIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("비인증 관리 요청은 로그인 화면으로 Redirect된다")
	void adminRequest_missingSession_redirectsToLogin() throws Exception {
		mockMvc.perform(get("/admin/posts"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/login"));
	}

	@Test
	@DisplayName("로그인 주소는 관리자 Session 검사에서 제외되어 정상 화면을 반환한다")
	void loginRequest_missingSession_returnsLoginView() throws Exception {
		mockMvc.perform(get("/admin/login"))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("공통 CSS는 관리자 Session 없이 조회할 수 있다")
	void cssRequest_missingSession_returnsStaticResource() throws Exception {
		mockMvc.perform(get("/css/app.css"))
				.andExpect(status().isOk());
	}
}
