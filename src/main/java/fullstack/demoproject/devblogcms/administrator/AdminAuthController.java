package fullstack.demoproject.devblogcms.administrator;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fullstack.demoproject.devblogcms._core.config.Define;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * 관리자 로그인 Form, 인증 요청과 로그아웃 요청을 처리한다.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {

	private static final String LOGIN_VIEW = "admin/login";

	private final AdministratorService administratorService;

	/**
	 * 관리자 로그인 Form을 표시한다.
	 *
	 * @param model 화면 Model
	 * @return 관리자 로그인 View
	 */
	@GetMapping("/login")
	public String loginForm(Model model) {
		prepareLoginModel(model, new AdministratorRequest.Login());
		return LOGIN_VIEW;
	}

	/**
	 * 로그인 입력을 직접 검증하고 인증된 관리자 정보만 Session에 저장한다.
	 *
	 * @param loginRequest 로그인 Form 입력
	 * @param request HTTP 요청
	 * @param model 화면 Model
	 * @return 성공 시 게시글 관리 Redirect, 실패 시 로그인 View
	 */
	@PostMapping("/login")
	public String login(
			AdministratorRequest.Login loginRequest,
			HttpServletRequest request,
			Model model
	) {
		if (!loginRequest.validate()) {
			prepareLoginModel(model, loginRequest);
			return LOGIN_VIEW;
		}

		Optional<AdministratorSession> administratorSession = administratorService.authenticate(
				loginRequest.getUsername(),
				loginRequest.getPassword()
		);
		if (administratorSession.isEmpty()) {
			prepareLoginModel(model, loginRequest);
			model.addAttribute("errorMessage", "아이디 또는 비밀번호를 확인해 주세요.");
			return LOGIN_VIEW;
		}

		HttpSession session = request.getSession(true);
		if (!session.isNew()) {
			request.changeSessionId();
		}
		session.setAttribute(Define.SESSION_ADMIN, administratorSession.get());
		return "redirect:/admin/posts";
	}

	/**
	 * 현재 관리자 Session 전체를 무효화하고 공개 게시글 목록으로 이동한다.
	 *
	 * @param request HTTP 요청
	 * @return 공개 게시글 목록 Redirect
	 */
	@PostMapping("/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return "redirect:/";
	}

	/**
	 * 로그인 Form과 직접 검증한 오류 메시지를 화면 Model에 담는다.
	 *
	 * @param model 화면 Model
	 * @param loginRequest 로그인 Form 입력과 검증 결과
	 */
	private void prepareLoginModel(Model model, AdministratorRequest.Login loginRequest) {
		model.addAttribute("pageTitle", "관리자 로그인");
		model.addAttribute("loginRequest", loginRequest);
		model.addAttribute("usernameError", loginRequest.getUsernameError());
		model.addAttribute("hasUsernameError", !loginRequest.getUsernameError().isEmpty());
		model.addAttribute("passwordError", loginRequest.getPasswordError());
		model.addAttribute("hasPasswordError", !loginRequest.getPasswordError().isEmpty());
	}
}
