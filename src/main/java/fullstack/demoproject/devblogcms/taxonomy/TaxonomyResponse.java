package fullstack.demoproject.devblogcms.taxonomy;

import java.util.List;

/**
 * 카테고리와 태그 관리 화면에 표시할 조회 결과를 정의한다.
 */
public final class TaxonomyResponse {

	private TaxonomyResponse() {
	}

	/**
	 * 카테고리 또는 태그 한 항목과 사용 게시글 수를 표현한다.
	 *
	 * @param id 분류 ID
	 * @param name 분류 이름
	 * @param postCount 사용 게시글 수
	 */
	public record Item(Long id, String name, long postCount) {
	}

	/**
	 * 관리 화면에서 함께 표시할 카테고리와 태그 목록을 표현한다.
	 *
	 * @param categories 카테고리 목록
	 * @param tags 태그 목록
	 */
	public record Management(List<Item> categories, List<Item> tags) {

		public Management {
			categories = List.copyOf(categories);
			tags = List.copyOf(tags);
		}
	}
}
