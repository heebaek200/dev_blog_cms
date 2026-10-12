package fullstack.demoproject.devblogcms.taxonomy;

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
import fullstack.demoproject.devblogcms.category.Category;
import fullstack.demoproject.devblogcms.category.CategoryRepository;
import fullstack.demoproject.devblogcms.post.Post;
import fullstack.demoproject.devblogcms.post.PostRepository;
import fullstack.demoproject.devblogcms.tag.TagRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminTaxonomyControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private PostRepository postRepository;

	@Test
	@DisplayName("인증된 관리자는 카테고리와 태그 관리 화면을 조회한다")
	void showTaxonomies_authenticated_returnsManagementView() throws Exception {
		mockMvc.perform(get("/admin/taxonomies").session(authenticatedSession()))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/taxonomies"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("카테고리·태그 관리")));
	}

	@Test
	@DisplayName("카테고리 이름이 비어 있으면 저장하지 않고 Field Error를 표시한다")
	void createCategory_blankName_returnsValidationError() throws Exception {
		mockMvc.perform(post("/admin/categories")
					.session(authenticatedSession())
					.param("name", "   "))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/taxonomies"))
				.andExpect(model().attribute("categoryNameError", "이름을 입력해 주세요."));

		assertThat(categoryRepository.count()).isZero();
	}

	@Test
	@DisplayName("카테고리 생성에 성공하면 성공 메시지와 함께 관리 화면으로 Redirect한다")
	void createCategory_validName_redirectsWithSuccessMessage() throws Exception {
		mockMvc.perform(post("/admin/categories")
					.session(authenticatedSession())
					.param("name", "  Spring  "))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/taxonomies"))
				.andExpect(flash().attribute("successMessage", "카테고리를 추가했습니다."));

		assertThat(categoryRepository.existsByNormalizedName("spring")).isTrue();
	}

	@Test
	@DisplayName("중복 태그 생성은 오류 메시지와 함께 관리 화면으로 Redirect한다")
	void createTag_duplicateName_redirectsWithErrorMessage() throws Exception {
		tagRepository.saveAndFlush(fullstack.demoproject.devblogcms.tag.Tag.create("JPA"));

		mockMvc.perform(post("/admin/tags")
					.session(authenticatedSession())
					.param("name", "  jpa  "))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/taxonomies"))
				.andExpect(flash().attribute(
						"errorMessage",
						"같은 이름의 카테고리 또는 태그가 이미 존재합니다."
				));
	}

	@Test
	@DisplayName("사용 중인 카테고리 삭제는 오류 메시지와 함께 관리 화면으로 Redirect한다")
	void deleteCategory_inUse_redirectsWithErrorMessage() throws Exception {
		Category category = categoryRepository.saveAndFlush(Category.create("Spring"));
		postRepository.saveAndFlush(Post.create(
				category,
				"사용 중인 카테고리",
				"사용 중인 카테고리 본문",
				Post.Status.DRAFT
		));

		mockMvc.perform(post("/admin/categories/{categoryId}/delete", category.getId())
					.session(authenticatedSession()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/taxonomies"))
				.andExpect(flash().attribute(
						"errorMessage",
						"게시글에서 사용 중인 카테고리 또는 태그는 삭제할 수 없습니다."
				));

		assertThat(categoryRepository.existsById(category.getId())).isTrue();
	}

	@Test
	@DisplayName("사용하지 않는 태그 삭제에 성공하면 성공 메시지와 함께 Redirect한다")
	void deleteTag_unused_redirectsWithSuccessMessage() throws Exception {
		var tag = tagRepository.saveAndFlush(fullstack.demoproject.devblogcms.tag.Tag.create("Unused"));

		mockMvc.perform(post("/admin/tags/{tagId}/delete", tag.getId())
					.session(authenticatedSession()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/taxonomies"))
				.andExpect(flash().attribute("successMessage", "태그를 삭제했습니다."));

		assertThat(tagRepository.existsById(tag.getId())).isFalse();
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
