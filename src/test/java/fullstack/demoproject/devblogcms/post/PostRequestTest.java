package fullstack.demoproject.devblogcms.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostRequestTest {

	@Test
	@DisplayName("게시글 입력을 검증하면 제목과 본문의 앞뒤 공백을 제거한다")
	void validate_validInput_trimsTextAndReturnsTrue() {
		PostRequest.Save request = new PostRequest.Save();
		request.setTitle("  게시글 제목  ");
		request.setContent("  게시글 본문  ");
		request.setStatus(Post.Status.DRAFT);

		boolean valid = request.validate();

		assertThat(valid).isTrue();
		assertThat(request.getTitle()).isEqualTo("게시글 제목");
		assertThat(request.getContent()).isEqualTo("게시글 본문");
	}

	@Test
	@DisplayName("필수 입력과 상태가 없으면 각 검증 오류를 저장한다")
	void validate_missingRequiredInput_storesErrors() {
		PostRequest.Save request = new PostRequest.Save();
		request.setTitle(" ");
		request.setContent(null);

		boolean valid = request.validate();

		assertThat(valid).isFalse();
		assertThat(request.getTitleError()).isEqualTo("제목을 입력해 주세요.");
		assertThat(request.getContentError()).isEqualTo("내용을 입력해 주세요.");
		assertThat(request.getStatusError()).isEqualTo("저장할 게시글 상태를 선택해 주세요.");
	}

	@Test
	@DisplayName("태그를 5개보다 많이 선택하면 검증 오류를 저장한다")
	void validate_moreThanFiveTags_storesTagError() {
		PostRequest.Save request = new PostRequest.Save();
		request.setTitle("게시글 제목");
		request.setContent("게시글 본문");
		request.setStatus(Post.Status.PUBLISHED);
		request.setTagIds(new LinkedHashSet<>(java.util.List.of(1L, 2L, 3L, 4L, 5L, 6L)));

		boolean valid = request.validate();

		assertThat(valid).isFalse();
		assertThat(request.getTagIdsError()).isEqualTo("태그는 최대 5개까지 지정할 수 있습니다.");
	}
}
