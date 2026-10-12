package fullstack.demoproject.devblogcms._core.exception;

/**
 * 정규화한 카테고리 또는 태그 이름이 이미 존재할 때 발생한다.
 */
public class DuplicateTaxonomyException extends RuntimeException {

	private DuplicateTaxonomyException(String taxonomyName) {
		super("같은 이름의 " + taxonomyName + "가 이미 존재합니다.");
	}

	/**
	 * 중복 카테고리 이름에 사용할 안전한 사용자 메시지로 예외를 생성한다.
	 *
	 * @return 카테고리 중복 예외
	 */
	public static DuplicateTaxonomyException forCategory() {
		return new DuplicateTaxonomyException("카테고리");
	}

	/**
	 * 중복 태그 이름에 사용할 안전한 사용자 메시지로 예외를 생성한다.
	 *
	 * @return 태그 중복 예외
	 */
	public static DuplicateTaxonomyException forTag() {
		return new DuplicateTaxonomyException("태그");
	}
}
