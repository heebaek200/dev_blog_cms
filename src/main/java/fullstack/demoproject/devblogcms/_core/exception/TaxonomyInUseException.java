package fullstack.demoproject.devblogcms._core.exception;

/**
 * 게시글이 사용 중인 카테고리 또는 태그를 삭제하려 할 때 발생한다.
 */
public class TaxonomyInUseException extends RuntimeException {

	/**
	 * 사용 중인 분류 삭제 차단에 사용할 안전한 사용자 메시지로 예외를 생성한다.
	 */
	public TaxonomyInUseException() {
		super("게시글에서 사용 중인 카테고리 또는 태그는 삭제할 수 없습니다.");
	}
}
