package fullstack.demoproject.devblogcms.taxonomy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 카테고리와 태그 관리 화면에서 전달되는 Form 입력을 정의한다.
 */
public final class TaxonomyRequest {

	private TaxonomyRequest() {
	}

	/**
	 * 새 카테고리 또는 태그의 이름을 전달하고 검증 결과를 보관한다.
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Create {

		private String name = "";

		private String nameError = "";

		/**
		 * 이름의 앞뒤 공백을 제거하고 필수값과 최대 길이를 검증한다.
		 *
		 * @return 이름이 유효하면 {@code true}, 그렇지 않으면 {@code false}
		 */
		public boolean validate() {
			name = name == null ? "" : name.strip();
			nameError = "";

			if (name.isEmpty()) {
				nameError = "이름을 입력해 주세요.";
			} else if (name.length() > 50) {
				nameError = "이름은 50자 이하여야 합니다.";
			}

			return nameError.isEmpty();
		}
	}
}
