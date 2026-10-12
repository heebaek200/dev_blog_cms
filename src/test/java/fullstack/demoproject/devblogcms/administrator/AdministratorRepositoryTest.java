package fullstack.demoproject.devblogcms.administrator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import fullstack.demoproject.devblogcms._core.config.JpaConfig;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class AdministratorRepositoryTest {

	@Autowired
	private AdministratorRepository administratorRepository;

	@Test
	@DisplayName("관리자를 저장하면 생성·수정 시각이 자동으로 기록된다")
	void save_validAdministrator_recordsAuditTimestamps() {
		Administrator administrator = Administrator.create("repository-admin", "bcrypt-hash-for-test");

		Administrator savedAdministrator = administratorRepository.saveAndFlush(administrator);

		assertThat(savedAdministrator.getCreatedAt()).isNotNull();
		assertThat(savedAdministrator.getUpdatedAt()).isNotNull();
	}
}
