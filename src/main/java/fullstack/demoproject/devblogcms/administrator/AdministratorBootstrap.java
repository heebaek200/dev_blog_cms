package fullstack.demoproject.devblogcms.administrator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 관리자 계정이 없을 때 환경변수로 최초 관리자 한 명을 안전하게 생성한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdministratorBootstrap implements ApplicationRunner {

	private final AdministratorRepository administratorRepository;
	private final PasswordEncoder passwordEncoder;
	private final Environment environment;

	/**
	 * 기존 관리자가 없을 때만 초기 자격 증명을 검사하고 BCrypt Hash 계정을 저장한다.
	 *
	 * @param args 애플리케이션 실행 인자
	 * @throws IllegalStateException 최초 관리자 환경변수가 없거나 올바르지 않을 때
	 */
	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (administratorRepository.count() > 0) {
			return;
		}

		String username = getBootstrapValue("initial-admin.username", "INITIAL_ADMIN_USERNAME");
		String password = getBootstrapValue("initial-admin.password", "INITIAL_ADMIN_PASSWORD");
		if (username == null || password == null) {
			throw new IllegalStateException(
					"관리자가 없습니다. INITIAL_ADMIN_USERNAME과 INITIAL_ADMIN_PASSWORD를 모두 설정해야 합니다.");
		}

		try {
			Administrator administrator = Administrator.create(username, passwordEncoder.encode(password));
			administratorRepository.save(administrator);
			log.info("Initial administrator account created.");
		} catch (IllegalArgumentException exception) {
			throw new IllegalStateException("최초 관리자 환경변수 형식이 올바르지 않습니다.", exception);
		}
	}

	/**
	 * Test Profile 속성과 실제 환경변수 중 비어 있지 않은 초기화 값을 찾는다.
	 *
	 * @param propertyName Spring 속성 이름
	 * @param environmentName 환경변수 이름
	 * @return Trim 처리한 값 또는 값이 없을 때 null
	 */
	private String getBootstrapValue(String propertyName, String environmentName) {
		String value = environment.getProperty(propertyName);
		if (value == null || value.isBlank()) {
			value = environment.getProperty(environmentName);
		}
		return value == null || value.isBlank() ? null : value.trim();
	}
}
