package fullstack.demoproject.devblogcms.administrator;

import fullstack.demoproject.devblogcms._core.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CMS를 운영하는 단일 관리자의 로그인 식별자와 Hash 비밀번호를 저장한다.
 */
@Getter
@Entity
@Table(name = "administrator")
@NoArgsConstructor
public class Administrator extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "administrator_id")
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	private Administrator(String username, String passwordHash) {
		this.username = username;
		this.passwordHash = passwordHash;
	}

	/**
	 * 검증된 최초 관리자 정보로 Entity를 생성한다.
	 *
	 * @param username 관리자 로그인 아이디
	 * @param passwordHash BCrypt로 변환한 비밀번호 Hash
	 * @return 저장 가능한 관리자 Entity
	 * @throws IllegalArgumentException 아이디 또는 Hash가 비어 있거나 아이디가 50자를 초과할 때
	 */
	public static Administrator create(String username, String passwordHash) {
		String normalizedUsername = username == null ? "" : username.trim();
		if (normalizedUsername.isEmpty() || normalizedUsername.length() > 50) {
			throw new IllegalArgumentException("관리자 아이디는 1자 이상 50자 이하여야 합니다.");
		}
		if (passwordHash == null || passwordHash.isBlank()) {
			throw new IllegalArgumentException("관리자 비밀번호 Hash는 필수입니다.");
		}
		return new Administrator(normalizedUsername, passwordHash);
	}
}
