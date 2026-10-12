package fullstack.demoproject.devblogcms.post;

import java.util.List;
import java.util.Set;

/**
 * 관리자 게시글 목록과 작성·수정 Form에 표시할 조회 결과를 정의한다.
 */
public final class PostResponse {

	private PostResponse() {
	}

	/**
	 * 관리자 게시글 목록의 한 항목을 표현한다.
	 *
	 * @param id 게시글 ID
	 * @param title 제목
	 * @param categoryName 카테고리 이름 또는 미지정 표시
	 * @param updatedAt 화면에 표시할 수정 시각
	 * @param published 공개 상태 여부
	 * @param draft 초안 상태 여부
	 */
	public record ListItem(
			Long id,
			String title,
			String categoryName,
			String updatedAt,
			boolean published,
			boolean draft
	) {
	}

	/**
	 * 수정 Form을 채우기 위한 저장된 게시글 값을 표현한다.
	 *
	 * @param id 게시글 ID
	 * @param title 제목
	 * @param content 본문
	 * @param categoryId 선택한 카테고리 ID
	 * @param tagIds 선택한 태그 ID 집합
	 * @param status 게시글 상태
	 */
	public record EditForm(
			Long id,
			String title,
			String content,
			Long categoryId,
			Set<Long> tagIds,
			Post.Status status
	) {
	}

	/**
	 * 카테고리 또는 태그 선택 항목을 표현한다.
	 *
	 * @param id 분류 ID
	 * @param name 분류 이름
	 * @param selected 현재 게시글에서 선택한 항목인지 여부
	 */
	public record Option(Long id, String name, boolean selected) {
	}

	/**
	 * 작성·수정 Form의 카테고리와 태그 선택 항목을 표현한다.
	 *
	 * @param categories 카테고리 선택 항목
	 * @param tags 태그 선택 항목
	 */
	public record FormOptions(List<Option> categories, List<Option> tags) {
	}
}
