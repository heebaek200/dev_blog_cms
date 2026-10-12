package fullstack.demoproject.devblogcms._core.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import fullstack.demoproject.devblogcms._core.config.Define;
import fullstack.demoproject.devblogcms._core.exception.Exception401;
import fullstack.demoproject.devblogcms.administrator.AdministratorSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 보호된 관리자 요청에서 기존 Session의 인증 정보만 검사한다.
 */
@Component
public class AdminAuthenticationInterceptor implements HandlerInterceptor {

	/**
	 * 새 Session을 만들지 않고 관리자 인증 정보가 있는 요청만 통과시킨다.
	 *
	 * @param request HTTP 요청
	 * @param response HTTP 응답
	 * @param handler 선택된 Handler
	 * @return 인증된 요청이면 true
	 * @throws Exception401 관리자 인증 정보가 없을 때
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		HttpSession session = request.getSession(false);
		Object sessionAdmin = session == null ? null : session.getAttribute(Define.SESSION_ADMIN);
		if (!(sessionAdmin instanceof AdministratorSession)) {
			throw new Exception401();
		}
		return true;
	}
}
