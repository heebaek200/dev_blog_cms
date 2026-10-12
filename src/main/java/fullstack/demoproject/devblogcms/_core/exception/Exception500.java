package fullstack.demoproject.devblogcms._core.exception;

/**
 * 애플리케이션이 명시적으로 내부 처리 실패를 보고할 때 발생한다.
 */
public class Exception500 extends RuntimeException {

	/**
	 * 원인을 보존하되 사용자 화면에는 고정된 안전한 문구만 표시한다.
	 *
	 * @param message Log에서 사용할 내부 처리 문맥
	 * @param cause 원인이 된 예외
	 */
	public Exception500(String message, Throwable cause) {
		super(message, cause);
	}
}
