package fullstack.demoproject.devblogcms._core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * MVC 요청의 공통 예외를 Redirect 또는 안전한 오류 화면으로 변환한다.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 미인증 관리자 요청을 로그인 화면으로 이동시킨다.
	 *
	 * @param exception 인증 실패 예외
	 * @param redirectAttributes 다음 요청에 전달할 메시지
	 * @return 관리자 로그인 Redirect 경로
	 */
	@ExceptionHandler(Exception401.class)
	public String handleUnauthorized(Exception401 exception, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
		return "redirect:/admin/login";
	}

	/**
	 * 없는 자원 요청을 HTTP 404와 공통 오류 화면으로 처리한다.
	 *
	 * @param exception 자원 조회 실패 예외
	 * @param response HTTP 응답
	 * @param model 오류 화면 Model
	 * @return 404 오류 View
	 */
	@ExceptionHandler(Exception404.class)
	public String handleNotFound(Exception404 exception, HttpServletResponse response, Model model) {
		response.setStatus(HttpStatus.NOT_FOUND.value());
		model.addAttribute("errorTitle", "페이지를 찾을 수 없습니다");
		model.addAttribute("errorMessage", exception.getMessage());
		return "error/404";
	}

	/**
	 * 분류 관련 업무 충돌을 관리 화면의 오류 메시지로 전달한다.
	 *
	 * @param exception 사용자에게 표시 가능한 업무 예외
	 * @param redirectAttributes 다음 요청에 전달할 메시지
	 * @return 카테고리·태그 관리 Redirect 경로
	 */
	@ExceptionHandler({DuplicateTaxonomyException.class, TaxonomyInUseException.class})
	public String handleTaxonomyConflict(RuntimeException exception, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
		return "redirect:/admin/taxonomies";
	}

	/**
	 * 잘못된 게시글 상태 전환을 게시글 관리 화면의 오류 메시지로 전달한다.
	 *
	 * @param exception 상태 전환 예외
	 * @param redirectAttributes 다음 요청에 전달할 메시지
	 * @return 게시글 관리 Redirect 경로
	 */
	@ExceptionHandler(InvalidPostStateException.class)
	public String handleInvalidPostState(InvalidPostStateException exception,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
		return "redirect:/admin/posts";
	}

	/**
	 * 처리되지 않은 실행 예외를 기록하고 내부 정보를 숨긴 HTTP 500 화면을 반환한다.
	 *
	 * @param exception 처리되지 않은 실행 예외
	 * @param response HTTP 응답
	 * @param model 오류 화면 Model
	 * @return 500 오류 View
	 */
	@ExceptionHandler(RuntimeException.class)
	public String handleInternalError(RuntimeException exception, HttpServletResponse response, Model model) {
		log.error("Unhandled application error.", exception);
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		model.addAttribute("errorTitle", "요청을 처리할 수 없습니다");
		model.addAttribute("errorMessage", "잠시 후 다시 시도해 주세요.");
		return "error/500";
	}
}
