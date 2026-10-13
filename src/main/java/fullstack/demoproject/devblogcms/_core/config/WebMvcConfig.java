package fullstack.demoproject.devblogcms._core.config;

import fullstack.demoproject.devblogcms._core.interceptor.SessionUserInterceptor;
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

	// 보호된 관리자 요청에서 기존 Session의 인증 정보만 검사하는 인터셉터
	private final AdminAuthenticationInterceptor adminAuthenticationInterceptor;

	// 공개 화면에 로그인 정보를 전달하는 인터셉터
	private final SessionUserInterceptor sessionUserInterceptor;

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

		registry.addInterceptor(sessionUserInterceptor)
				.addPathPatterns("/**");
	}
}
