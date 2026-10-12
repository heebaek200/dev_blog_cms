package fullstack.demoproject.devblogcms._core.exception;

/**
 * 요청한 자원이 없거나 공개할 수 없을 때 발생한다.
 */
public class Exception404 extends RuntimeException {

	/**
	 * 내부 식별정보를 포함하지 않는 안전한 사유로 예외를 생성한다.
	 *
	 * @param message 자원을 찾을 수 없는 사유
	 */
	public Exception404(String message) {
		super(message);
	}
}
