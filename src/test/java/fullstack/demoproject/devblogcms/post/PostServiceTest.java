package fullstack.demoproject.devblogcms.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import fullstack.demoproject.devblogcms._core.exception.InvalidPostStateException;
import fullstack.demoproject.devblogcms.category.Category;
import fullstack.demoproject.devblogcms.category.CategoryRepository;
import fullstack.demoproject.devblogcms.tag.Tag;
import fullstack.demoproject.devblogcms.tag.TagRepository;
import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostServiceTest {

	@Autowired
	private PostService postService;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	@DisplayName("카테고리가 없는 게시글 목록은 미지정으로 표시한다")
	void findAll_postWithoutCategory_returnsUncategorizedLabel() {
		postRepository.saveAndFlush(Post.create(null, "미분류 글", "미분류 본문", Post.Status.DRAFT));

		var posts = postService.findAll();

		assertThat(posts).singleElement().satisfies(post -> {
			assertThat(post.categoryName()).isEqualTo("미지정");
			assertThat(post.draft()).isTrue();
			assertThat(post.published()).isFalse();
		});
	}

	@Test
	@DisplayName("게시글을 생성하면 선택한 카테고리와 태그를 함께 저장한다")
	void create_validRequest_savesPostWithTaxonomies() {
		Category category = categoryRepository.saveAndFlush(Category.create("Spring"));
		Tag tag = tagRepository.saveAndFlush(Tag.create("JPA"));
		PostRequest.Save request = request("새 게시글", "새 게시글 본문", Post.Status.DRAFT);
		request.setCategoryId(category.getId());
		request.setTagIds(new LinkedHashSet<>(java.util.List.of(tag.getId())));

		postService.create(request);
		entityManager.flush();
		entityManager.clear();

		Post savedPost = postRepository.findAllWithTaxonomiesOrderByCreatedAtDesc().getFirst();
		assertThat(savedPost.getCategory().getName()).isEqualTo("Spring");
		assertThat(savedPost.getPostTags()).extracting(postTag -> postTag.getTag().getName())
				.containsExactly("JPA");
	}

	@Test
	@DisplayName("게시글을 수정하면 내용, 상태와 태그 연결을 모두 변경한다")
	void update_validRequest_changesPostAndReplacesTags() {
		Tag oldTag = tagRepository.saveAndFlush(Tag.create("Old"));
		Tag newTag = tagRepository.saveAndFlush(Tag.create("New"));
		Post existingPost = Post.create(null, "기존 제목", "기존 본문", Post.Status.DRAFT);
		existingPost.addTag(oldTag);
		Post savedPost = postRepository.saveAndFlush(existingPost);
		PostRequest.Save request = request("수정 제목", "수정 본문", Post.Status.PUBLISHED);
		request.setTagIds(new LinkedHashSet<>(java.util.List.of(newTag.getId())));

		postService.update(savedPost.getId(), request);
		entityManager.flush();
		entityManager.clear();

		Post updatedPost = postRepository.findDetailById(savedPost.getId()).orElseThrow();
		assertThat(updatedPost.getTitle()).isEqualTo("수정 제목");
		assertThat(updatedPost.getStatus()).isEqualTo(Post.Status.PUBLISHED);
		assertThat(updatedPost.getPostTags()).extracting(postTag -> postTag.getTag().getName())
				.containsExactly("New");
	}

	@Test
	@DisplayName("존재하지 않는 카테고리 ID는 Field Error로 기록하고 게시글을 저장하지 않는다")
	void create_missingCategory_returnsFieldError() {
		PostRequest.Save request = request("잘못된 분류", "잘못된 분류 본문", Post.Status.DRAFT);
		request.setCategoryId(999L);

		boolean created = postService.create(request);

		assertThat(created).isFalse();
		assertThat(request.getCategoryIdError()).isEqualTo("선택한 카테고리를 찾을 수 없습니다.");
		assertThat(postRepository.count()).isZero();
	}

	@Test
	@DisplayName("이미 공개된 게시글의 공개 요청은 거부한다")
	void publish_alreadyPublished_throwsInvalidStateException() {
		Post post = postRepository.saveAndFlush(
				Post.create(null, "공개 게시글", "공개 게시글 본문", Post.Status.PUBLISHED)
		);

		assertThatThrownBy(() -> postService.publish(post.getId()))
				.isInstanceOf(InvalidPostStateException.class);
	}

	@Test
	@DisplayName("게시글을 삭제하면 저장소에서 제거한다")
	void delete_existingPost_removesPost() {
		Post post = postRepository.saveAndFlush(
				Post.create(null, "삭제 게시글", "삭제 게시글 본문", Post.Status.DRAFT)
		);

		postService.delete(post.getId());
		entityManager.flush();

		assertThat(postRepository.existsById(post.getId())).isFalse();
	}

	/**
	 * 테스트에 사용할 기본 게시글 저장 요청을 생성한다.
	 *
	 * @param title 제목
	 * @param content 본문
	 * @param status 상태
	 * @return 게시글 저장 요청
	 */
	private PostRequest.Save request(String title, String content, Post.Status status) {
		PostRequest.Save request = new PostRequest.Save();
		request.setTitle(title);
		request.setContent(content);
		request.setStatus(status);
		return request;
	}
}
