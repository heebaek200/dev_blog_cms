package fullstack.demoproject.devblogcms.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 카테고리 CRUD를 담당한다.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 카테고리와 각 카테고리를 사용하는 게시글 수를 이름순으로 조회한다.
     *
     * @return 카테고리별 사용 현황
     */
    @Query("""
            SELECT c.id AS id
                 , c.name AS name
                 , COUNT(p.id) AS postCount
              FROM Category c
              LEFT JOIN Post p ON p.category = c
             GROUP BY c.id, c.name
             ORDER BY c.name
            """)
    List<UsageCount> findAllWithPostCount();

    /**
     * 카테고리명(정규화) 중복 체크
     *
     * @param normalizedName 카테고리명(정규화)
     * @return 존재하면 true
     */
    boolean existsByNormalizedName(String normalizedName);

    /**
     * 지정한 카테고리를 사용하는 게시글 수를 조회한다.
     *
     * @param categoryId 카테고리 ID
     * @return 카테고리를 사용하는 게시글 수
     */
    @Query("""
            SELECT COUNT(p.id)
              FROM Post p
             WHERE p.category.id = :categoryId
            """)
    long countPostsById(@Param("categoryId") Long categoryId);

    /**
     * 카테고리 목록에 필요한 식별자, 이름과 사용 게시글 수를 제공한다.
     */
    interface UsageCount {

        Long getId();

        String getName();

        long getPostCount();
    }

}
