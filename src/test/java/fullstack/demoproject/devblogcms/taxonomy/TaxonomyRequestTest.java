package fullstack.demoproject.devblogcms.taxonomy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaxonomyRequestTest {

	@Test
	@DisplayName("분류 이름을 검증하면 앞뒤 공백을 제거한다")
	void validate_validName_trimsNameAndReturnsTrue() {
		TaxonomyRequest.Create request = new TaxonomyRequest.Create();
		request.setName("  Spring Boot  ");

		boolean valid = request.validate();

		assertThat(valid).isTrue();
		assertThat(request.getName()).isEqualTo("Spring Boot");
		assertThat(request.getNameError()).isEmpty();
	}

	@Test
	@DisplayName("분류 이름이 비어 있으면 필수 오류를 저장한다")
	void validate_blankName_storesRequiredError() {
		TaxonomyRequest.Create request = new TaxonomyRequest.Create();
		request.setName("   ");

		boolean valid = request.validate();

		assertThat(valid).isFalse();
		assertThat(request.getNameError()).isEqualTo("이름을 입력해 주세요.");
	}

	@Test
	@DisplayName("분류 이름이 50자를 넘으면 길이 오류를 저장한다")
	void validate_tooLongName_storesLengthError() {
		TaxonomyRequest.Create request = new TaxonomyRequest.Create();
		request.setName("가".repeat(51));

		boolean valid = request.validate();

		assertThat(valid).isFalse();
		assertThat(request.getNameError()).isEqualTo("이름은 50자 이하여야 합니다.");
	}
}
