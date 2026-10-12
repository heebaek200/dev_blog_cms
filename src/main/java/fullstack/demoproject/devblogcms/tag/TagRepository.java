package fullstack.demoproject.devblogcms.tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 태그 CRUD를 담당한다.
 */
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 태그와 각 태그를 사용하는 게시글 수를 이름순으로 조회한다.
     *
     * @return 태그별 사용 현황
     */
    @Query("""
            SELECT t.id AS id
                 , t.name AS name
                 , COUNT(pt.id) AS postCount
              FROM Tag t
              LEFT JOIN PostTag pt ON pt.tag = t
             GROUP BY t.id, t.name
             ORDER BY t.name
            """)
    List<UsageCount> findAllWithPostCount();

    /**
     * 태그명(정규화) 중복 체크
     *
     * @param normalizedName 태그명(정규화)
     * @return 존재하면 true
     */
    boolean existsByNormalizedName(String normalizedName);

    /**
     * 지정한 태그를 사용하는 게시글 수를 조회한다.
     *
     * @param tagId 태그 ID
     * @return 태그를 사용하는 게시글 수
     */
    @Query("""
            SELECT COUNT(pt.id)
              FROM PostTag pt
             WHERE pt.tag.id = :tagId
            """)
    long countPostsById(@Param("tagId") Long tagId);

    /**
     * 태그 목록에 필요한 식별자, 이름과 사용 게시글 수를 제공한다.
     */
    interface UsageCount {

        Long getId();

        String getName();

        long getPostCount();
    }

}
