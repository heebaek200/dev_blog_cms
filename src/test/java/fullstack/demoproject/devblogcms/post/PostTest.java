package fullstack.demoproject.devblogcms.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fullstack.demoproject.devblogcms._core.exception.InvalidPostStateException;
import fullstack.demoproject.devblogcms.category.Category;
import fullstack.demoproject.devblogcms.tag.Tag;

class PostTest {

	@Test
	@DisplayName("게시글을 수정하면 제목과 본문을 정리하고 카테고리를 변경한다")
	void update_validInput_changesPostFields() {
		Post post = Post.create(null, "기존 제목", "기존 본문", Post.Status.DRAFT);
		Category category = Category.create("Spring");

		post.update(category, "  수정 제목  ", "  수정 본문  ");

		assertThat(post.getCategory()).isSameAs(category);
		assertThat(post.getTitle()).isEqualTo("수정 제목");
		assertThat(post.getContent()).isEqualTo("수정 본문");
	}

	@Test
	@DisplayName("태그를 교체하면 유지할 연결은 남기고 선택 해제한 연결은 제거한다")
	void replaceTags_changedSelection_replacesPostTags() {
		Post post = Post.create(null, "태그 변경", "태그 변경 본문", Post.Status.DRAFT);
		Tag retainedTag = Tag.create("JPA");
		Tag removedTag = Tag.create("Java");
		Tag addedTag = Tag.create("Spring");
		post.addTag(retainedTag);
		post.addTag(removedTag);

		post.replaceTags(List.of(retainedTag, addedTag));

		assertThat(post.getPostTags())
				.extracting(PostTag::getTag)
				.containsExactlyInAnyOrder(retainedTag, addedTag);
	}

	@Test
	@DisplayName("초안은 공개로, 공개 게시글은 다시 초안으로 전환할 수 있다")
	void changeStatus_validTransitions_changesStatus() {
		Post post = Post.create(null, "상태 전환", "상태 전환 본문", Post.Status.DRAFT);

		post.publish();
		assertThat(post.getStatus()).isEqualTo(Post.Status.PUBLISHED);

		post.draft();
		assertThat(post.getStatus()).isEqualTo(Post.Status.DRAFT);
	}

	@Test
	@DisplayName("현재 상태와 같은 상태 전환 요청은 거부한다")
	void publish_alreadyPublished_throwsInvalidStateException() {
		Post post = Post.create(null, "공개 글", "공개 글 본문", Post.Status.PUBLISHED);

		assertThatThrownBy(post::publish)
				.isInstanceOf(InvalidPostStateException.class);
	}
}
