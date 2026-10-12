package fullstack.demoproject.devblogcms.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import fullstack.demoproject.devblogcms._core.config.JpaConfig;
import fullstack.demoproject.devblogcms.category.Category;
import fullstack.demoproject.devblogcms.category.CategoryRepository;
import fullstack.demoproject.devblogcms.tag.Tag;
import fullstack.demoproject.devblogcms.tag.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("게시글 상세는 카테고리와 모든 태그를 함께 조회한다")
    void findDetailById_existingPost_fetchesAllTaxonomies() {
        Category category = categoryRepository.saveAndFlush(Category.create("Spring"));
        Tag jpaTag = tagRepository.saveAndFlush(Tag.create("JPA"));
        Tag testTag = tagRepository.saveAndFlush(Tag.create("Test"));
        Post savedPost = savePost(category, "JPA 연관관계", Post.Status.PUBLISHED, jpaTag, testTag);
        entityManager.clear();

        Post foundPost = postRepository.findDetailById(savedPost.getId()).orElseThrow();
        PersistenceUnitUtil persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        assertThat(persistenceUnitUtil.isLoaded(foundPost, "category")).isTrue();
        assertThat(persistenceUnitUtil.isLoaded(foundPost, "postTags")).isTrue();
        assertThat(foundPost.getCategory().getName()).isEqualTo("Spring");
        assertThat(foundPost.getPostTags())
                .extracting(postTag -> postTag.getTag().getName())
                .containsExactlyInAnyOrder("JPA", "Test");
        assertThat(foundPost.getPostTags())
                .allSatisfy(postTag -> assertThat(persistenceUnitUtil.isLoaded(postTag, "tag")).isTrue());
    }

    @Test
    @DisplayName("공개 상세 조회에서는 초안 게시글을 반환하지 않는다")
    void findDetailByIdAndStatus_draftPost_returnsEmptyForPublishedStatus() {
        Post draftPost = savePost(null, "비공개 초안", Post.Status.DRAFT);
        entityManager.clear();

        assertThat(postRepository.findDetailByIdAndStatus(draftPost.getId(), Post.Status.PUBLISHED))
                .isEmpty();
        assertThat(postRepository.findDetailByIdAndStatus(draftPost.getId(), Post.Status.DRAFT))
                .isPresent();
    }

    @Test
    @DisplayName("상태별 게시글 목록은 해당 상태만 최근 생성 순으로 조회한다")
    void findAllByStatus_publishedStatus_returnsPublishedPostsNewestFirst() {
        savePost(null, "첫 번째 공개 글", Post.Status.PUBLISHED);
        savePost(null, "비공개 초안", Post.Status.DRAFT);
        savePost(null, "두 번째 공개 글", Post.Status.PUBLISHED);
        entityManager.clear();

        List<Post> posts = postRepository.findAllByStatusWithTaxonomiesOrderByCreatedAtDesc(
                Post.Status.PUBLISHED
        );

        assertThat(posts).extracting(Post::getTitle)
                .containsExactlyInAnyOrder("첫 번째 공개 글", "두 번째 공개 글");
        assertThat(posts).extracting(Post::getStatus)
                .containsOnly(Post.Status.PUBLISHED);
        assertThat(posts).extracting(Post::getCreatedAt)
                .isSortedAccordingTo(Comparator.nullsLast(Comparator.<LocalDateTime>reverseOrder()));
    }

    @Test
    @DisplayName("카테고리별 공개 목록은 지정한 카테고리의 공개 게시글만 조회한다")
    void findAllByCategoryIdAndStatus_matchingCondition_returnsOnlyMatchingPosts() {
        Category spring = categoryRepository.saveAndFlush(Category.create("Spring"));
        Category java = categoryRepository.saveAndFlush(Category.create("Java"));
        savePost(spring, "Spring 공개 글", Post.Status.PUBLISHED);
        savePost(spring, "Spring 초안", Post.Status.DRAFT);
        savePost(java, "Java 공개 글", Post.Status.PUBLISHED);
        entityManager.clear();

        List<Post> posts = postRepository.findAllByCategoryIdAndStatusWithTaxonomiesOrderByCreatedAtDesc(
                spring.getId(),
                Post.Status.PUBLISHED
        );

        assertThat(posts).extracting(Post::getTitle).containsExactly("Spring 공개 글");
    }

    @Test
    @DisplayName("태그별 공개 목록은 조건 태그로 필터링하면서 게시글의 다른 태그도 함께 조회한다")
    void findAllByTagIdAndStatus_matchingTag_fetchesAllPostTags() {
        Tag jpaTag = tagRepository.saveAndFlush(Tag.create("JPA"));
        Tag springTag = tagRepository.saveAndFlush(Tag.create("Spring"));
        Tag javaTag = tagRepository.saveAndFlush(Tag.create("Java"));
        savePost(null, "JPA 게시글", Post.Status.PUBLISHED, jpaTag, springTag);
        savePost(null, "Java 게시글", Post.Status.PUBLISHED, javaTag);
        savePost(null, "JPA 초안", Post.Status.DRAFT, jpaTag);
        entityManager.clear();

        List<Post> posts = postRepository.findAllByTagIdAndStatusWithTaxonomiesOrderByCreatedAtDesc(
                jpaTag.getId(),
                Post.Status.PUBLISHED
        );

        assertThat(posts).extracting(Post::getTitle).containsExactly("JPA 게시글");
        assertThat(posts.getFirst().getPostTags())
                .extracting(postTag -> postTag.getTag().getName())
                .containsExactlyInAnyOrder("JPA", "Spring");
    }

    @Test
    @DisplayName("카테고리 없이도 게시글을 저장하고 조회할 수 있다")
    void save_withoutCategory_persistsPost() {
        Post savedPost = savePost(null, "미분류 게시글", Post.Status.DRAFT);
        entityManager.clear();

        Post foundPost = postRepository.findDetailById(savedPost.getId()).orElseThrow();

        assertThat(foundPost.getCategory()).isNull();
    }

    /**
     * 테스트 조건에 맞는 게시글과 태그 연결을 저장한다.
     *
     * @param category 선택할 카테고리
     * @param title 게시글 제목
     * @param status 게시글 상태
     * @param tags 연결할 태그
     * @return 저장된 게시글
     */
    private Post savePost(Category category, String title, Post.Status status, Tag... tags) {
        Post post = Post.create(category, title, title + " 본문", status);
        for (Tag tag : tags) {
            post.addTag(tag);
        }
        return postRepository.saveAndFlush(post);
    }
}
