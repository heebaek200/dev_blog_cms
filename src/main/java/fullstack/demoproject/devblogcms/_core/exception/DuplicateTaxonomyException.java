package fullstack.demoproject.devblogcms._core.exception;

/**
 * 정규화한 카테고리 또는 태그 이름이 이미 존재할 때 발생한다.
 */
public class DuplicateTaxonomyException extends RuntimeException {

	/**
	 * 중복 분류 이름에 사용할 안전한 사용자 메시지로 예외를 생성한다.
	 */
	public DuplicateTaxonomyException() {
		super("같은 이름의 카테고리 또는 태그가 이미 존재합니다.");
	}
}
