package fullstack.demoproject.devblogcms._core.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(InternalServerErrorTest.ErrorTestConfiguration.class)
class InternalServerErrorTest {

	private static final String INTERNAL_DETAIL = "노출되면 안 되는 내부 오류 상세";

	@LocalServerPort
	private int port;

	@Test
	@DisplayName("처리되지 않은 오류는 내부 정보를 숨긴 공통 500 화면으로 응답한다")
	void internalError_requested_returnsSafe500Page() throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + port + "/test/internal-error"))
				.header("Accept", "text/html")
				.GET()
				.build();

		HttpResponse<String> response = HttpClient.newHttpClient()
				.send(request, HttpResponse.BodyHandlers.ofString());

		assertThat(response.statusCode()).isEqualTo(500);
		assertThat(response.body()).contains("요청을 처리할 수 없습니다", "잠시 후 다시 시도해 주세요.");
		assertThat(response.body()).doesNotContain(INTERNAL_DETAIL, "RuntimeException", "Stacktrace");
	}

	/**
	 * 500 응답을 실제 HTTP 요청으로 검증하기 위한 Test 전용 Controller를 등록한다.
	 */
	@TestConfiguration
	static class ErrorTestConfiguration {

		/**
		 * Test 전용 오류 Controller를 Spring Bean으로 제공한다.
		 *
		 * @return 내부 오류를 발생시키는 Controller
		 */
		@Bean
		FailingController failingController() {
			return new FailingController();
		}
	}

	/**
	 * 공통 예외 처리기에 전달할 처리되지 않은 오류를 발생시킨다.
	 */
	@Controller
	static class FailingController {

		/**
		 * 의도적으로 내부 오류를 발생시킨다.
		 *
		 * @return 정상적으로 반환되지 않음
		 * @throws RuntimeException 항상 발생하는 Test 오류
		 */
		@GetMapping("/test/internal-error")
		String fail() {
			throw new RuntimeException(INTERNAL_DETAIL);
		}
	}
}
