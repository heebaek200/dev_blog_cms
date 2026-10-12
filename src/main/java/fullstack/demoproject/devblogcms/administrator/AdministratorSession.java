package fullstack.demoproject.devblogcms.administrator;

import java.io.Serializable;

/**
 * HTTP Session에 Entity 대신 보관하는 최소 관리자 인증 정보다.
 *
 * @param administratorId 관리자 식별자
 * @param username 관리자 로그인 아이디
 */
public record AdministratorSession(Long administratorId, String username) implements Serializable {
}
