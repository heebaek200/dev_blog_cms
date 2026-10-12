package fullstack.demoproject.devblogcms.administrator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;

class AdministratorBootstrapUnitTest {

	@Test
	@DisplayName("관리자가 없고 초기 환경변수도 없으면 시작을 명확한 오류로 중단한다")
	void run_missingAdministratorAndCredentials_throwsClearException() {
		AdministratorRepository repository = mock(AdministratorRepository.class);
		when(repository.count()).thenReturn(0L);
		AdministratorBootstrap bootstrap = new AdministratorBootstrap(
				repository,
				mock(PasswordEncoder.class),
				new MockEnvironment());

		assertThatThrownBy(() -> bootstrap.run(new DefaultApplicationArguments(new String[0])))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("INITIAL_ADMIN_USERNAME")
				.hasMessageContaining("INITIAL_ADMIN_PASSWORD");
	}

	@Test
	@DisplayName("관리자가 이미 존재하면 초기 환경변수가 없어도 시작을 계속한다")
	void run_existingAdministratorWithoutCredentials_completesNormally() {
		AdministratorRepository repository = mock(AdministratorRepository.class);
		when(repository.count()).thenReturn(1L);
		AdministratorBootstrap bootstrap = new AdministratorBootstrap(
				repository,
				mock(PasswordEncoder.class),
				new MockEnvironment());

		assertThatCode(() -> bootstrap.run(new DefaultApplicationArguments(new String[0])))
				.doesNotThrowAnyException();
	}
}
