package fullstack.demoproject.devblogcms.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import fullstack.demoproject.devblogcms._core.config.Define;
import fullstack.demoproject.devblogcms.administrator.AdministratorSession;
import fullstack.demoproject.devblogcms.category.Category;
import fullstack.demoproject.devblogcms.category.CategoryRepository;
import fullstack.demoproject.devblogcms.tag.Tag;
import fullstack.demoproject.devblogcms.tag.TagRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PublicPostControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private TagRepository tagRepository;

	@Test
	@DisplayName("공개 목록은 공개 글과 분류 Link만 표시하고 초안은 숨긴다")
	void showPosts_mixedStatuses_rendersPublishedPostOnly() throws Exception {
		Category category = categoryRepository.saveAndFlush(Category.create("Spring"));
		Tag tag = tagRepository.saveAndFlush(Tag.create("JPA"));
		savePost(category, "공개 게시글", Post.Status.PUBLISHED, tag);
		savePost(category, "숨겨진 초안", Post.Status.DRAFT, tag);

		MvcResult result = mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("public/posts"))
				.andExpect(model().attribute("publicNavigation", true))
				.andExpect(content().string(Matchers.containsString("href=\"/admin/login\"")))
				.andExpect(content().string(Matchers.not(Matchers.containsString("href=\"/admin/posts\""))))
				.andExpect(content().string(Matchers.containsString("공개 게시글")))
				.andExpect(content().string(Matchers.not(Matchers.containsString("숨겨진 초안"))))
				.andExpect(content().string(Matchers.containsString("/categories/" + category.getId())))
				.andExpect(content().string(Matchers.containsString("/tags/" + tag.getId())))
				.andReturn();

		assertThat(result.getRequest().getSession(false)).isNull();
	}

	@Test
	@DisplayName("로그인한 관리자의 공개 화면에는 관리 화면 Link만 표시한다")
	void showPosts_authenticatedAdministrator_rendersAdminLinkOnly() throws Exception {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(Define.SESSION_ADMIN, new AdministratorSession(1L, "test-admin"));

		mockMvc.perform(get("/").session(session))
				.andExpect(status().isOk())
				.andExpect(model().attribute(Define.SESSION_ADMIN,
						new AdministratorSession(1L, "test-admin")))
				.andExpect(content().string(Matchers.containsString("href=\"/admin/posts\"")))
				.andExpect(content().string(Matchers.not(Matchers.containsString("href=\"/admin/login\""))));
	}

	@Test
	@DisplayName("카테고리와 태그 목록은 선택한 분류의 공개 글만 표시한다")
	void showPostsByTaxonomy_existingTaxonomies_filterPublishedPosts() throws Exception {
		Category spring = categoryRepository.saveAndFlush(Category.create("Spring"));
		Category java = categoryRepository.saveAndFlush(Category.create("Java"));
		Tag jpa = tagRepository.saveAndFlush(Tag.create("JPA"));
		Tag web = tagRepository.saveAndFlush(Tag.create("Web"));
		savePost(spring, "JPA 공개 글", Post.Status.PUBLISHED, jpa);
		savePost(java, "Web 공개 글", Post.Status.PUBLISHED, web);

		mockMvc.perform(get("/categories/{categoryId}", spring.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(Matchers.containsString("JPA 공개 글")))
				.andExpect(content().string(Matchers.not(Matchers.containsString("Web 공개 글"))));

		mockMvc.perform(get("/tags/{tagId}", web.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(Matchers.containsString("Web 공개 글")))
				.andExpect(content().string(Matchers.not(Matchers.containsString("JPA 공개 글"))));
	}

	@Test
	@DisplayName("게시글이 없는 기존 분류는 빈 상태를 표시한다")
	void showPostsByCategory_withoutPosts_rendersEmptyState() throws Exception {
		Category category = categoryRepository.saveAndFlush(Category.create("빈 카테고리"));

		mockMvc.perform(get("/categories/{categoryId}", category.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name("public/posts"))
				.andExpect(content().string(Matchers.containsString("선택한 조건의 글이 없습니다.")));
	}

	@Test
	@DisplayName("공개 게시글 상세는 제목, 본문과 분류 Link를 표시한다")
	void showPost_publishedPost_rendersDetail() throws Exception {
		Category category = categoryRepository.saveAndFlush(Category.create("Java"));
		Tag tag = tagRepository.saveAndFlush(Tag.create("문법"));
		Post post = savePost(category, "공개 상세", Post.Status.PUBLISHED, tag);

		mockMvc.perform(get("/posts/{postId}", post.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name("public/post-detail"))
				.andExpect(content().string(Matchers.containsString("공개 상세")))
				.andExpect(content().string(Matchers.containsString("공개 상세 본문")))
				.andExpect(content().string(Matchers.containsString("#문법")));
	}

	@Test
	@DisplayName("초안과 존재하지 않는 게시글 상세는 같은 공통 404 화면을 반환한다")
	void showPost_draftOrMissingPost_returnsNotFound() throws Exception {
		Post draft = savePost(null, "비공개 초안", Post.Status.DRAFT);

		mockMvc.perform(get("/posts/{postId}", draft.getId()))
				.andExpect(status().isNotFound())
				.andExpect(view().name("error/404"))
				.andExpect(model().attribute("errorMessage", "게시글을 찾을 수 없습니다."));

		mockMvc.perform(get("/posts/{postId}", 999L))
				.andExpect(status().isNotFound())
				.andExpect(view().name("error/404"))
				.andExpect(model().attribute("errorMessage", "게시글을 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("존재하지 않는 카테고리와 태그는 공통 404 화면을 반환한다")
	void showPostsByTaxonomy_missingTaxonomy_returnsNotFound() throws Exception {
		mockMvc.perform(get("/categories/{categoryId}", 999L))
				.andExpect(status().isNotFound())
				.andExpect(view().name("error/404"));

		mockMvc.perform(get("/tags/{tagId}", 999L))
				.andExpect(status().isNotFound())
				.andExpect(view().name("error/404"));
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
