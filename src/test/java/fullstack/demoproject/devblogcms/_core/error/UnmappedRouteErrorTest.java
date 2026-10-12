package fullstack.demoproject.devblogcms._core.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UnmappedRouteErrorTest {

	@LocalServerPort
	private int port;

	@Test
	@DisplayName("등록되지 않은 주소는 공통 404 화면으로 응답한다")
	void unmappedRoute_requested_returnsSafe404Page() throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + port + "/route-that-does-not-exist"))
				.header("Accept", "text/html")
				.GET()
				.build();

		HttpResponse<String> response = HttpClient.newHttpClient()
				.send(request, HttpResponse.BodyHandlers.ofString());

		assertThat(response.statusCode()).isEqualTo(404);
		assertThat(response.body()).contains("페이지를 찾을 수 없습니다");
		assertThat(response.body()).doesNotContain("Exception", "Stacktrace");
	}
}
