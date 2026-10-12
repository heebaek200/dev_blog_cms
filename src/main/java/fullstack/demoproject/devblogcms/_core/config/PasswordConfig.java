package fullstack.demoproject.devblogcms._core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 관리자 비밀번호를 단방향 Hash로 저장하기 위한 Encoder를 제공한다.
 */
@Configuration
public class PasswordConfig {

	/**
	 * BCrypt 기반 PasswordEncoder를 생성한다.
	 *
	 * @return 애플리케이션에서 공유할 PasswordEncoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
