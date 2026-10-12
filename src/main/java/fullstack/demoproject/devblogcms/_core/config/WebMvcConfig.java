package fullstack.demoproject.devblogcms._core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import fullstack.demoproject.devblogcms._core.interceptor.AdminAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;

/**
 * 관리자 Session Interceptor의 적용 경로와 공개 예외 경로를 구성한다.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final AdminAuthenticationInterceptor adminAuthenticationInterceptor;

	/**
	 * 모든 관리자 경로를 보호하되 로그인과 공통 공개 리소스는 검사에서 제외한다.
	 *
	 * @param registry MVC Interceptor Registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(adminAuthenticationInterceptor)
				.addPathPatterns("/admin/**")
				.excludePathPatterns("/admin/login", "/css/**", "/error", "/favicon.ico");
	}
}
