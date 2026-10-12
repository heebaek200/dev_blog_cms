package fullstack.demoproject.devblogcms.administrator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AdministratorBootstrapTest {

	@Autowired
	private AdministratorRepository administratorRepository;

	@Autowired
	private AdministratorBootstrap administratorBootstrap;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("최초 실행은 Test 전용 자격 증명의 BCrypt 관리자 한 명을 생성한다")
	void bootstrap_missingAdministrator_createsHashedAdministrator() {
		Administrator administrator = administratorRepository.findByUsername("test-admin").orElseThrow();

		assertThat(administratorRepository.count()).isEqualTo(1);
		assertThat(administrator.getPasswordHash()).isNotEqualTo("test-password-only");
		assertThat(passwordEncoder.matches("test-password-only", administrator.getPasswordHash())).isTrue();
	}

	@Test
	@DisplayName("관리자가 이미 존재하면 최초 관리자 계정을 추가하지 않는다")
	void bootstrap_existingAdministrator_doesNotCreateAnotherAdministrator() throws Exception {
		administratorBootstrap.run(new DefaultApplicationArguments(new String[0]));

		assertThat(administratorRepository.count()).isEqualTo(1);
	}
}
