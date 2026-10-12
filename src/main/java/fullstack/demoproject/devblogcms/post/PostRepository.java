package fullstack.demoproject.devblogcms.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 게시글과 게시글에 연결된 카테고리·태그를 조회하는 저장소다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 상태와 관계없이 게시글 상세와 연결된 모든 분류 정보를 조회한다.
     *
     * @param postId 게시글 ID
     * @return 게시글이 있으면 분류 정보가 초기화된 게시글
     */
    @Query("""
            SELECT DISTINCT p
              FROM Post p
              LEFT JOIN FETCH p.category
              LEFT JOIN FETCH p.postTags pt
              LEFT JOIN FETCH pt.tag
             WHERE p.id = :postId
            """)
    Optional<Post> findDetailById(@Param("postId") Long postId);

    /**
     * 지정한 상태의 게시글 상세와 연결된 모든 분류 정보를 조회한다.
     * 공개 상세 조회에서는 {@link Post.Status#PUBLISHED}를 전달한다.
     *
     * @param postId 게시글 ID
     * @param status 조회할 게시글 상태
     * @return 조건을 만족하는 게시글이 있으면 분류 정보가 초기화된 게시글
     */
    @Query("""
            SELECT DISTINCT p
              FROM Post p
              LEFT JOIN FETCH p.category
              LEFT JOIN FETCH p.postTags pt
              LEFT JOIN FETCH pt.tag
             WHERE p.id = :postId
               AND p.status = :status
            """)
    Optional<Post> findDetailByIdAndStatus(
            @Param("postId") Long postId,
            @Param("status") Post.Status status
    );

    /**
     * 모든 게시글과 연결된 분류 정보를 최근 생성 순으로 조회한다.
     *
     * @return 분류 정보가 초기화된 게시글 목록
     */
    @Query("""
            SELECT DISTINCT p
              FROM Post p
              LEFT JOIN FETCH p.category
              LEFT JOIN FETCH p.postTags pt
              LEFT JOIN FETCH pt.tag
             ORDER BY p.createdAt DESC
            """)
    List<Post> findAllWithTaxonomiesOrderByCreatedAtDesc();

    /**
     * 지정한 상태의 게시글과 연결된 분류 정보를 최근 생성 순으로 조회한다.
     * 공개 목록 조회에서는 {@link Post.Status#PUBLISHED}를 전달한다.
     *
     * @param status 조회할 게시글 상태
     * @return 조건을 만족하는 게시글 목록
     */
    @Query("""
            SELECT DISTINCT p
              FROM Post p
              LEFT JOIN FETCH p.category
              LEFT JOIN FETCH p.postTags pt
              LEFT JOIN FETCH pt.tag
             WHERE p.status = :status
             ORDER BY p.createdAt DESC
            """)
    List<Post> findAllByStatusWithTaxonomiesOrderByCreatedAtDesc(
            @Param("status") Post.Status status
    );

    /**
     * 지정한 카테고리와 상태에 해당하는 게시글을 최근 생성 순으로 조회한다.
     *
     * @param categoryId 카테고리 ID
     * @param status 조회할 게시글 상태
     * @return 조건을 만족하는 게시글 목록
     */
    @Query("""
            SELECT DISTINCT p
              FROM Post p
              LEFT JOIN FETCH p.category
              LEFT JOIN FETCH p.postTags pt
              LEFT JOIN FETCH pt.tag
             WHERE p.category.id = :categoryId
               AND p.status = :status
             ORDER BY p.createdAt DESC
            """)
    List<Post> findAllByCategoryIdAndStatusWithTaxonomiesOrderByCreatedAtDesc(
            @Param("categoryId") Long categoryId,
            @Param("status") Post.Status status
    );

    /**
     * 지정한 태그와 상태에 해당하는 게시글을 최근 생성 순으로 조회한다.
     * 태그 필터는 EXISTS로 분리하여 조회 결과 게시글의 다른 태그도 모두 Fetch Join한다.
     *
     * @param tagId 태그 ID
     * @param status 조회할 게시글 상태
     * @return 조건을 만족하는 게시글 목록
     */
    @Query("""
            SELECT DISTINCT p
              FROM Post p
              LEFT JOIN FETCH p.category
              LEFT JOIN FETCH p.postTags pt
              LEFT JOIN FETCH pt.tag
             WHERE p.status = :status
               AND EXISTS (
                       SELECT filterPt.id
                         FROM PostTag filterPt
                        WHERE filterPt.post = p
                          AND filterPt.tag.id = :tagId
                   )
             ORDER BY p.createdAt DESC
            """)
    List<Post> findAllByTagIdAndStatusWithTaxonomiesOrderByCreatedAtDesc(
            @Param("tagId") Long tagId,
            @Param("status") Post.Status status
    );
}
