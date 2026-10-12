package fullstack.demoproject.devblogcms.post;

import java.util.List;

/**
 * 공개 게시글 목록과 상세 화면에 표시할 조회 결과를 정의한다.
 */
public final class PublicPostResponse {

	private PublicPostResponse() {
	}

	/**
	 * 카테고리 또는 태그 필터 Link를 표현한다.
	 *
	 * @param name 화면에 표시할 이름
	 * @param url 공개 목록 필터 URL
	 * @param selected 현재 적용된 필터인지 여부
	 */
	public record FilterOption(String name, String url, boolean selected) {
	}

	/**
	 * 게시글에 연결된 카테고리 Link를 표현한다.
	 *
	 * @param name 카테고리 이름
	 * @param url 카테고리별 공개 목록 URL
	 */
	public record CategoryLink(String name, String url) {
	}

	/**
	 * 게시글에 연결된 태그 Link를 표현한다.
	 *
	 * @param name 태그 이름
	 * @param url 태그별 공개 목록 URL
	 */
	public record TagLink(String name, String url) {
	}

	/**
	 * 공개 게시글 목록의 한 항목을 표현한다.
	 *
	 * @param id 게시글 ID
	 * @param title 제목
	 * @param createdAt 화면에 표시할 작성일
	 * @param category 연결된 카테고리 또는 null
	 * @param tags 연결된 태그 목록
	 * @param hasTags 연결된 태그 존재 여부
	 */
	public record ListItem(
			Long id,
			String title,
			String createdAt,
			CategoryLink category,
			List<TagLink> tags,
			boolean hasTags
	) {
	}

	/**
	 * 공개 게시글 목록 화면 전체 정보를 표현한다.
	 *
	 * @param pageTitle 브라우저 제목
	 * @param listLabel 목록 보조 제목
	 * @param listTitle 목록 제목
	 * @param postCountLabel 게시글 수 문구
	 * @param emptyTitle 빈 상태 제목
	 * @param emptyDescription 빈 상태 설명
	 * @param allSelected 전체 필터 선택 여부
	 * @param categories 카테고리 필터 목록
	 * @param tags 태그 필터 목록
	 * @param posts 공개 게시글 목록
	 * @param hasPosts 공개 게시글 존재 여부
	 */
	public record ListView(
			String pageTitle,
			String listLabel,
			String listTitle,
			String postCountLabel,
			String emptyTitle,
			String emptyDescription,
			boolean allSelected,
			List<FilterOption> categories,
			List<FilterOption> tags,
			List<ListItem> posts,
			boolean hasPosts
	) {
	}

	/**
	 * 공개 게시글 상세 화면 정보를 표현한다.
	 *
	 * @param pageTitle 브라우저 제목
	 * @param title 게시글 제목
	 * @param content 게시글 본문
	 * @param createdAt 화면에 표시할 작성일
	 * @param category 연결된 카테고리 또는 null
	 * @param tags 연결된 태그 목록
	 * @param hasTags 연결된 태그 존재 여부
	 */
	public record Detail(
			String pageTitle,
			String title,
			String content,
			String createdAt,
			CategoryLink category,
			List<TagLink> tags,
			boolean hasTags
	) {
	}
}
