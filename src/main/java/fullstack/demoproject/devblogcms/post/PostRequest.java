package fullstack.demoproject.devblogcms.post;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관리자 게시글 작성·수정 Form에서 전달되는 입력을 정의한다.
 */
public final class PostRequest {

	private PostRequest() {
	}

	/**
	 * 게시글 제목, 본문, 상태와 선택한 분류 정보를 전달한다.
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Save {

		private String title = "";

		private String content = "";

		private Long categoryId;

		private Set<Long> tagIds = new LinkedHashSet<>();

		private Post.Status status;

		private String titleError = "";

		private String contentError = "";

		private String categoryIdError = "";

		private String tagIdsError = "";

		private String statusError = "";

		/**
		 * 게시글 Form 입력을 정리하고 필수값, 길이와 태그 개수를 검증한다.
		 *
		 * @return 모든 입력값이 유효하면 {@code true}, 그렇지 않으면 {@code false}
		 */
		public boolean validate() {
			title = title == null ? "" : title.strip();
			titleError = "";
			if (title.isEmpty()) {
				titleError = "제목을 입력해 주세요.";
			} else if (title.length() > 200) {
				titleError = "제목은 200자 이하여야 합니다.";
			}

			content = content == null ? "" : content.strip();
			contentError = "";
			if (content.isEmpty()) {
				contentError = "내용을 입력해 주세요.";
			}

			tagIds = tagIds == null ? new LinkedHashSet<>() : new LinkedHashSet<>(tagIds);
			tagIdsError = "";
			if (tagIds.size() > 5) {
				tagIdsError = "태그는 최대 5개까지 지정할 수 있습니다.";
			}

			statusError = "";
			if (status == null) {
				statusError = "저장할 게시글 상태를 선택해 주세요.";
			}

			return titleError.isEmpty()
					&& contentError.isEmpty()
					&& tagIdsError.isEmpty()
					&& statusError.isEmpty();
		}
	}
}
