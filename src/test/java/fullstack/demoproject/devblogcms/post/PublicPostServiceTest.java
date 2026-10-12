package fullstack.demoproject.devblogcms.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import fullstack.demoproject.devblogcms._core.exception.Exception404;
import fullstack.demoproject.devblogcms.category.Category;
import fullstack.demoproject.devblogcms.category.CategoryRepository;
import fullstack.demoproject.devblogcms.tag.Tag;
import fullstack.demoproject.devblogcms.tag.TagRepository;
import jakarta.persistence.EntityManager;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@ActiveProfiles("test")
@Transactional
class PublicPostServiceTest {

	@Autowired
	private PublicPostService publicPostService;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	@DisplayName("전체 공개 목록은 공개 게시글과 연결된 분류만 화면 정보로 반환한다")
	void findAll_mixedStatuses_returnsOnlyPublishedPosts() {
		Category category = categoryRepository.saveAndFlush(Category.create("Spring"));
		Tag tag = tagRepository.saveAndFlush(Tag.create("JPA"));
		savePost(category, "공개 글", Post.Status.PUBLISHED, tag);
		savePost(category, "비공개 초안", Post.Status.DRAFT, tag);
		entityManager.clear();

		PublicPostResponse.ListView result = publicPostService.findAll();

		assertThat(result.posts()).singleElement().satisfies(post -> {
			assertThat(post.title()).isEqualTo("공개 글");
			assertThat(post.category().name()).isEqualTo("Spring");
			assertThat(post.tags()).extracting(PublicPostResponse.TagLink::name).containsExactly("JPA");
		});
	}

	@Test
	@DisplayName("공개 목록은 게시글 수와 관계없이 세 개의 고정 Query로 조회한다")
	void findAll_multiplePosts_usesFixedQueryCount() {
		Category category = categoryRepository.saveAndFlush(Category.create("Java"));
		Tag tag = tagRepository.saveAndFlush(Tag.create("Test"));
		savePost(category, "첫 번째 글", Post.Status.PUBLISHED, tag);
		savePost(category, "두 번째 글", Post.Status.PUBLISHED, tag);
		entityManager.clear();
		Statistics statistics = entityManager.getEntityManagerFactory()
				.unwrap(SessionFactory.class)
				.getStatistics();
		statistics.clear();

		publicPostService.findAll();

		assertThat(statistics.getPrepareStatementCount()).isEqualTo(3);
	}

	@Test
	@DisplayName("공개 상세는 카테고리와 태그를 한 Query로 조회한다")
	void findDetail_publishedPost_usesSingleQuery() {
		Category category = categoryRepository.saveAndFlush(Category.create("JPA"));
		Tag tag = tagRepository.saveAndFlush(Tag.create("Hibernate"));
		Post post = savePost(category, "상세 공개 글", Post.Status.PUBLISHED, tag);
		entityManager.clear();
		Statistics statistics = entityManager.getEntityManagerFactory()
				.unwrap(SessionFactory.class)
				.getStatistics();
		statistics.clear();

		PublicPostResponse.Detail result = publicPostService.findDetail(post.getId());

		assertThat(result.title()).isEqualTo("상세 공개 글");
		assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("초안 게시글 상세는 존재 여부를 드러내지 않고 404로 처리한다")
	void findDetail_draftPost_throwsNotFoundException() {
		Post draft = savePost(null, "비공개 초안", Post.Status.DRAFT);
		entityManager.clear();

		assertThatThrownBy(() -> publicPostService.findDetail(draft.getId()))
				.isInstanceOf(Exception404.class)
				.hasMessage("게시글을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("존재하지 않는 카테고리와 태그 필터는 404로 처리한다")
	void findByTaxonomy_missingTaxonomy_throwsNotFoundException() {
		assertThatThrownBy(() -> publicPostService.findByCategory(999L))
				.isInstanceOf(Exception404.class)
				.hasMessage("카테고리를 찾을 수 없습니다.");
		assertThatThrownBy(() -> publicPostService.findByTag(999L))
				.isInstanceOf(Exception404.class)
				.hasMessage("태그를 찾을 수 없습니다.");
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
