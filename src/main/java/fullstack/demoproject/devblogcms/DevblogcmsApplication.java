package fullstack.demoproject.devblogcms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 개인 기술 블로그 CMS의 Spring Boot 애플리케이션 진입점이다.
 */
@SpringBootApplication
public class DevblogcmsApplication {

	/**
	 * Spring ApplicationContext와 내장 Web Server를 시작한다.
	 *
	 * @param args 애플리케이션 실행 인자
	 */
	public static void main(String[] args) {
		SpringApplication.run(DevblogcmsApplication.class, args);
	}

}
