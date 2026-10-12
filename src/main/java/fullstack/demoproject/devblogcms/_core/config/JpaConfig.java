package fullstack.demoproject.devblogcms._core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Entity의 생성·수정 시각을 자동으로 기록하도록 Auditing을 활성화한다.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
