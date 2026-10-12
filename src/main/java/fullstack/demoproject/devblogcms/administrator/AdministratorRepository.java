package fullstack.demoproject.devblogcms.administrator;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 관리자 계정의 저장과 로그인 식별자 조회를 담당한다.
 */
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

	/**
	 * 로그인 아이디로 관리자 계정을 조회한다.
	 *
	 * @param username 관리자 로그인 아이디
	 * @return 일치하는 관리자 계정
	 */
	Optional<Administrator> findByUsername(String username);
}
