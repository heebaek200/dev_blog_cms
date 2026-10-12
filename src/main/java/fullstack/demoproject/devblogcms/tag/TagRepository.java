package fullstack.demoproject.devblogcms.tag;


import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 태그 CRUD를 담당한다.
 */
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 태그명(정규화) 중복 체크
     *
     * @param normalizedName 태그명(정규화)
     * @return 존재하면 true
     */
    boolean existsByNormalizedName(String normalizedName);

}
