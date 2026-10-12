package fullstack.demoproject.devblogcms._core.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;

class ValidationFoundationTest {

	@Test
	@DisplayName("Jakarta Validation은 Form DTO의 빈 필드를 검출한다")
	void validate_blankFormField_returnsConstraintViolation() {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

		assertThat(validator.validate(new ValidationProbe(""))).hasSize(1);
	}

	private record ValidationProbe(@NotBlank String title) {
	}
}
