package fullstack.demoproject.devblogcms._core.exception;

/**
 * 게시글에 허용되지 않은 상태 전환을 요청할 때 발생한다.
 */
public class InvalidPostStateException extends RuntimeException {

	/**
	 * 잘못된 게시글 상태 전환에 사용할 안전한 사용자 메시지로 예외를 생성한다.
	 */
	public InvalidPostStateException() {
		super("현재 게시글 상태에서는 요청한 변경을 수행할 수 없습니다.");
	}
}
