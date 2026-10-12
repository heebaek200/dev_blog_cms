package fullstack.demoproject.devblogcms.administrator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdministratorServiceTest {

	@Mock
	private AdministratorRepository administratorRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private Administrator administrator;

	@InjectMocks
	private AdministratorService administratorService;

	@Test
	@DisplayName("올바른 자격 증명은 Entity 대신 최소 관리자 Session 정보를 반환한다")
	void authenticate_validCredentials_returnsAdministratorSession() {
		when(administratorRepository.findByUsername("admin")).thenReturn(Optional.of(administrator));
		when(administrator.getPasswordHash()).thenReturn("bcrypt-hash");
		when(passwordEncoder.matches("password", "bcrypt-hash")).thenReturn(true);
		when(administrator.getId()).thenReturn(1L);
		when(administrator.getUsername()).thenReturn("admin");

		Optional<AdministratorSession> result = administratorService.authenticate("  admin  ", "password");

		assertThat(result).contains(new AdministratorSession(1L, "admin"));
	}

	@Test
	@DisplayName("존재하지 않는 아이디는 인증 실패로 처리한다")
	void authenticate_unknownUsername_returnsEmpty() {
		when(administratorRepository.findByUsername("unknown")).thenReturn(Optional.empty());

		Optional<AdministratorSession> result = administratorService.authenticate("unknown", "password");

		assertThat(result).isEmpty();
		verifyNoInteractions(passwordEncoder);
	}

	@Test
	@DisplayName("일치하지 않는 비밀번호는 인증 실패로 처리한다")
	void authenticate_wrongPassword_returnsEmpty() {
		when(administratorRepository.findByUsername("admin")).thenReturn(Optional.of(administrator));
		when(administrator.getPasswordHash()).thenReturn("bcrypt-hash");
		when(passwordEncoder.matches("wrong-password", "bcrypt-hash")).thenReturn(false);

		Optional<AdministratorSession> result = administratorService.authenticate("admin", "wrong-password");

		assertThat(result).isEmpty();
	}
}
