package fullstack.demoproject.devblogcms.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import fullstack.demoproject.devblogcms._core.config.Define;
import fullstack.demoproject.devblogcms.administrator.AdministratorSession;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminPostControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PostRepository postRepository;

	@Test
	@DisplayName("인증된 관리자는 게시글 목록과 빈 상태를 조회한다")
	void showPosts_authenticated_returnsEmptyListView() throws Exception {
		mockMvc.perform(get("/admin/posts").session(authenticatedSession()))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/posts"))
				.andExpect(model().attribute("hasPosts", false))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("아직 작성한 글이 없습니다.")));
	}

	@Test
	@DisplayName("게시글 작성 화면은 검증 오류 없이 빈 Form을 표시한다")
	void showCreateForm_authenticated_returnsEmptyForm() throws Exception {
		mockMvc.perform(get("/admin/posts/new").session(authenticatedSession()))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/post-form"))
				.andExpect(model().attribute("isEdit", false))
				.andExpect(content().string(org.hamcrest.Matchers.not(
						org.hamcrest.Matchers.containsString("is-invalid")
				)));
	}

	@Test
	@DisplayName("게시글 입력이 올바르지 않으면 저장하지 않고 Field Error를 표시한다")
	void create_invalidInput_returnsFormWithErrors() throws Exception {
		mockMvc.perform(post("/admin/posts")
					.session(authenticatedSession())
					.param("title", " ")
					.param("content", " ")
					.param("status", "DRAFT"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/post-form"))
				.andExpect(model().attribute("titleError", "제목을 입력해 주세요."))
				.andExpect(model().attribute("contentError", "내용을 입력해 주세요."))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("is-invalid")));

		assertThat(postRepository.count()).isZero();
	}

	@Test
	@DisplayName("게시글 생성에 성공하면 성공 메시지와 함께 목록으로 Redirect한다")
	void create_validInput_redirectsWithSuccessMessage() throws Exception {
		mockMvc.perform(post("/admin/posts")
					.session(authenticatedSession())
					.param("title", "새 초안")
					.param("content", "새 초안 본문")
					.param("status", "DRAFT"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/posts"))
				.andExpect(flash().attribute("successMessage", "게시글을 초안으로 저장했습니다."));

		assertThat(postRepository.findAll()).singleElement()
				.extracting(Post::getTitle)
				.isEqualTo("새 초안");
	}

	@Test
	@DisplayName("수정 화면은 저장된 게시글 값을 Form에 다시 표시한다")
	void showEditForm_existingPost_returnsPopulatedForm() throws Exception {
		Post post = postRepository.saveAndFlush(
				Post.create(null, "수정할 글", "수정할 본문", Post.Status.DRAFT)
		);

		mockMvc.perform(get("/admin/posts/{postId}/edit", post.getId())
					.session(authenticatedSession()))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/post-form"))
				.andExpect(model().attribute("isEdit", true))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"수정할 글\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("수정할 본문")));
	}

	@Test
	@DisplayName("게시글 수정에 성공하면 변경 내용을 저장하고 목록으로 Redirect한다")
	void update_validInput_updatesPostAndRedirects() throws Exception {
		Post post = postRepository.saveAndFlush(
				Post.create(null, "기존 글", "기존 글 본문", Post.Status.DRAFT)
		);

		mockMvc.perform(post("/admin/posts/{postId}/edit", post.getId())
					.session(authenticatedSession())
					.param("title", "수정된 글")
					.param("content", "수정된 글 본문")
					.param("status", "PUBLISHED"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/posts"))
				.andExpect(flash().attribute("successMessage", "게시글을 수정했습니다."));

		Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
		assertThat(updatedPost.getTitle()).isEqualTo("수정된 글");
		assertThat(updatedPost.getStatus()).isEqualTo(Post.Status.PUBLISHED);
	}

	@Test
	@DisplayName("초안 게시글을 공개하면 성공 메시지와 함께 목록으로 Redirect한다")
	void publish_draftPost_redirectsWithSuccessMessage() throws Exception {
		Post post = postRepository.saveAndFlush(
				Post.create(null, "공개할 글", "공개할 글 본문", Post.Status.DRAFT)
		);

		mockMvc.perform(post("/admin/posts/{postId}/publish", post.getId())
					.session(authenticatedSession()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/posts"))
				.andExpect(flash().attribute("successMessage", "게시글을 공개했습니다."));

		assertThat(postRepository.findById(post.getId()).orElseThrow().getStatus())
				.isEqualTo(Post.Status.PUBLISHED);
	}

	@Test
	@DisplayName("이미 공개된 게시글의 공개 요청은 오류 메시지와 함께 목록으로 Redirect한다")
	void publish_alreadyPublished_redirectsWithErrorMessage() throws Exception {
		Post post = postRepository.saveAndFlush(
				Post.create(null, "공개 글", "공개 글 본문", Post.Status.PUBLISHED)
		);

		mockMvc.perform(post("/admin/posts/{postId}/publish", post.getId())
					.session(authenticatedSession()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/posts"))
				.andExpect(flash().attribute(
						"errorMessage",
						"현재 게시글 상태에서는 요청한 변경을 수행할 수 없습니다."
				));
	}

	@Test
	@DisplayName("게시글 삭제에 성공하면 성공 메시지와 함께 목록으로 Redirect한다")
	void delete_existingPost_redirectsWithSuccessMessage() throws Exception {
		Post post = postRepository.saveAndFlush(
				Post.create(null, "삭제할 글", "삭제할 글 본문", Post.Status.DRAFT)
		);

		mockMvc.perform(post("/admin/posts/{postId}/delete", post.getId())
					.session(authenticatedSession()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/posts"))
				.andExpect(flash().attribute("successMessage", "게시글을 삭제했습니다."));

		assertThat(postRepository.existsById(post.getId())).isFalse();
	}

	/**
	 * 관리자 요청을 통과할 수 있는 Test Session을 생성한다.
	 *
	 * @return 관리자 인증 정보가 저장된 Session
	 */
	private MockHttpSession authenticatedSession() {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(Define.SESSION_ADMIN, new AdministratorSession(1L, "test-admin"));
		return session;
	}
}
