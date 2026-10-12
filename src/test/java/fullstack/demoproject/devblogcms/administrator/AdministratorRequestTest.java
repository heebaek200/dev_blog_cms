package fullstack.demoproject.devblogcms.administrator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdministratorRequestTest {

	@Test
	@DisplayName("로그인 입력을 검증하면 아이디 앞뒤 공백을 제거한다")
	void validate_validInput_trimsUsernameAndReturnsTrue() {
		AdministratorRequest.Login request = new AdministratorRequest.Login();
		request.setUsername("  test-admin  ");
		request.setPassword("test-password-only");

		boolean valid = request.validate();

		assertThat(valid).isTrue();
		assertThat(request.getUsername()).isEqualTo("test-admin");
		assertThat(request.getUsernameError()).isEmpty();
		assertThat(request.getPasswordError()).isEmpty();
	}

	@Test
	@DisplayName("아이디와 비밀번호가 비어 있으면 각 필드 오류를 저장한다")
	void validate_blankInput_storesRequiredErrors() {
		AdministratorRequest.Login request = new AdministratorRequest.Login();
		request.setUsername("   ");
		request.setPassword("   ");

		boolean valid = request.validate();

		assertThat(valid).isFalse();
		assertThat(request.getUsernameError()).isEqualTo("아이디를 입력해 주세요.");
		assertThat(request.getPasswordError()).isEqualTo("비밀번호를 입력해 주세요.");
	}

	@Test
	@DisplayName("아이디가 50자를 넘으면 길이 오류를 저장한다")
	void validate_tooLongUsername_storesLengthError() {
		AdministratorRequest.Login request = new AdministratorRequest.Login();
		request.setUsername("a".repeat(51));
		request.setPassword("test-password-only");

		boolean valid = request.validate();

		assertThat(valid).isFalse();
		assertThat(request.getUsernameError()).isEqualTo("아이디는 50자 이하여야 합니다.");
	}
}
