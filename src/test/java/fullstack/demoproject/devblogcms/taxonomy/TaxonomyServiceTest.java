package fullstack.demoproject.devblogcms.taxonomy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import fullstack.demoproject.devblogcms._core.exception.DuplicateTaxonomyException;
import fullstack.demoproject.devblogcms._core.exception.TaxonomyInUseException;
import fullstack.demoproject.devblogcms.category.Category;
import fullstack.demoproject.devblogcms.category.CategoryRepository;
import fullstack.demoproject.devblogcms.post.Post;
import fullstack.demoproject.devblogcms.post.PostRepository;
import fullstack.demoproject.devblogcms.tag.Tag;
import fullstack.demoproject.devblogcms.tag.TagRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaxonomyServiceTest {

	@Autowired
	private TaxonomyService taxonomyService;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private PostRepository postRepository;

	@Test
	@DisplayName("카테고리와 태그는 정규화한 같은 이름으로 중복 생성할 수 없다")
	void create_duplicateNormalizedName_throwsDuplicateException() {
		taxonomyService.createCategory(" Spring ");
		taxonomyService.createTag(" JPA ");

		assertThatThrownBy(() -> taxonomyService.createCategory("spring"))
				.isInstanceOf(DuplicateTaxonomyException.class);
		assertThatThrownBy(() -> taxonomyService.createTag("  jpa  "))
				.isInstanceOf(DuplicateTaxonomyException.class);
	}

	@Test
	@DisplayName("분류 목록은 카테고리와 태그별 사용 게시글 수를 표시한다")
	void findAll_usedTaxonomies_returnsPostCounts() {
		Category category = categoryRepository.saveAndFlush(Category.create("Spring"));
		Tag tag = tagRepository.saveAndFlush(Tag.create("JPA"));
		Post post = Post.create(category, "분류 집계", "분류 집계 본문", Post.Status.PUBLISHED);
		post.addTag(tag);
		postRepository.saveAndFlush(post);

		TaxonomyResponse.Management result = taxonomyService.findAll();

		assertThat(result.categories())
				.extracting(TaxonomyResponse.Item::name, TaxonomyResponse.Item::postCount)
				.containsExactly(org.assertj.core.groups.Tuple.tuple("Spring", 1L));
		assertThat(result.tags())
				.extracting(TaxonomyResponse.Item::name, TaxonomyResponse.Item::postCount)
				.containsExactly(org.assertj.core.groups.Tuple.tuple("JPA", 1L));
	}

	@Test
	@DisplayName("게시글에서 사용 중인 카테고리와 태그는 삭제할 수 없다")
	void delete_inUseTaxonomies_throwsInUseException() {
		Category category = categoryRepository.saveAndFlush(Category.create("Spring"));
		Tag tag = tagRepository.saveAndFlush(Tag.create("JPA"));
		Post post = Post.create(category, "삭제 차단", "삭제 차단 본문", Post.Status.DRAFT);
		post.addTag(tag);
		postRepository.saveAndFlush(post);

		assertThatThrownBy(() -> taxonomyService.deleteCategory(category.getId()))
				.isInstanceOf(TaxonomyInUseException.class);
		assertThatThrownBy(() -> taxonomyService.deleteTag(tag.getId()))
				.isInstanceOf(TaxonomyInUseException.class);
	}

	@Test
	@DisplayName("게시글에서 사용하지 않는 카테고리와 태그는 삭제한다")
	void delete_unusedTaxonomies_deletesBoth() {
		Category category = categoryRepository.saveAndFlush(Category.create("Unused Category"));
		Tag tag = tagRepository.saveAndFlush(Tag.create("Unused Tag"));

		taxonomyService.deleteCategory(category.getId());
		taxonomyService.deleteTag(tag.getId());

		assertThat(categoryRepository.existsById(category.getId())).isFalse();
		assertThat(tagRepository.existsById(tag.getId())).isFalse();
	}
}
