package fullstack.demoproject.devblogcms.administrator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관리자 인증 화면에서 전달되는 Form 입력을 정의한다.
 */
public final class AdministratorRequest {

	private AdministratorRequest() {
	}

	/**
	 * 관리자 로그인 아이디와 비밀번호를 전달한다.
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Login {

		private String username = "";

		private String password = "";

		private String usernameError = "";

		private String passwordError = "";

		/**
		 * 로그인 입력값을 정리하고 필수값과 아이디 길이를 검증한다.
		 * 비밀번호는 실제 인증값이 달라지지 않도록 공백을 제거하지 않는다.
		 *
		 * @return 모든 입력값이 유효하면 {@code true}, 그렇지 않으면 {@code false}
		 */
		public boolean validate() {
			username = username == null ? "" : username.strip();
			password = password == null ? "" : password;
			usernameError = "";
			passwordError = "";

			if (username.isEmpty()) {
				usernameError = "아이디를 입력해 주세요.";
			} else if (username.length() > 50) {
				usernameError = "아이디는 50자 이하여야 합니다.";
			}

			if (password.isBlank()) {
				passwordError = "비밀번호를 입력해 주세요.";
			}

			return usernameError.isEmpty() && passwordError.isEmpty();
		}
	}
}
