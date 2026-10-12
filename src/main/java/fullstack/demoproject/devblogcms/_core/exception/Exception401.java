package fullstack.demoproject.devblogcms._core.exception;

/**
 * 관리자 Session이 없어 보호된 화면을 사용할 수 없을 때 발생한다.
 */
public class Exception401 extends RuntimeException {

	/**
	 * 로그인 필요 안내에 사용할 고정 메시지로 예외를 생성한다.
	 */
	public Exception401() {
		super("로그인이 필요한 요청입니다.");
	}
}
