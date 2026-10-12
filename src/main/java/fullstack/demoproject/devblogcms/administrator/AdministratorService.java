package fullstack.demoproject.devblogcms.administrator;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 최초 관리자 계정의 자격 증명을 확인하고 Session용 인증 정보를 생성한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdministratorService {

	private final AdministratorRepository administratorRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 로그인 아이디와 원문 비밀번호를 BCrypt Hash와 비교한다.
	 * 계정이 없거나 비밀번호가 다르면 동일하게 빈 결과를 반환한다.
	 *
	 * @param username 로그인 아이디
	 * @param password 원문 비밀번호
	 * @return 인증에 성공하면 Session에 저장할 최소 관리자 정보
	 */
	public Optional<AdministratorSession> authenticate(String username, String password) {
		String normalizedUsername = username == null ? "" : username.strip();
		String rawPassword = password == null ? "" : password;

		Optional<Administrator> administrator = administratorRepository.findByUsername(normalizedUsername);
		if (administrator.isEmpty()
				|| !passwordEncoder.matches(rawPassword, administrator.get().getPasswordHash())) {
			log.warn("Administrator login rejected.");
			return Optional.empty();
		}

		Administrator authenticatedAdministrator = administrator.get();
		return Optional.of(new AdministratorSession(
				authenticatedAdministrator.getId(),
				authenticatedAdministrator.getUsername()
		));
	}
}
