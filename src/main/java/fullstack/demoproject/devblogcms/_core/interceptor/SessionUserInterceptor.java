package fullstack.demoproject.devblogcms._core.interceptor;

import fullstack.demoproject.devblogcms._core.config.Define;
import fullstack.demoproject.devblogcms.administrator.AdministratorSession;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 공개 화면에 로그인 정보를 전달하는 인터셉터
 */
@Component
public class SessionUserInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

        // 1. 화면을 반환하는 요청만 처리
        // 데이터만 반환하는 요청은 동작시키지 않는다. (@ResponseBody, @RestController가 붙은 요청은 제외함)
        if (modelAndView == null) {
            return;
        }

        // 2. 리다이렉트 응답은 화면은 화면을 그리지 않으므로 건너뜀.
        String viewName = modelAndView.getViewName();
        if (viewName != null && viewName.startsWith("redirect:")) {
            return;
        }

        // 3. 세션이 있을 때만 조회 (없으면 새로만들지 않아야 함)
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        // 4. 로그인 정보가 있으면 화면에 데이터 추가
        AdministratorSession sessionUserDto = (AdministratorSession) session.getAttribute(Define.SESSION_ADMIN);
        if (sessionUserDto != null) {
            modelAndView.addObject(Define.SESSION_ADMIN, sessionUserDto);
        }

    }

}
