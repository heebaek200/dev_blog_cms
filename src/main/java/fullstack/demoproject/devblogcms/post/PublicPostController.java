package fullstack.demoproject.devblogcms.post;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;

/**
 * 방문자의 공개 게시글 목록·상세와 분류별 조회 요청을 처리한다.
 */
@Controller
@RequiredArgsConstructor
public class PublicPostController {
	private static final String LIST_VIEW = "public/posts";
	private static final String DETAIL_VIEW = "public/post-detail";

	private final PublicPostService publicPostService;

	/**
	 * 모든 공개 게시글을 최근 작성 순으로 표시한다.
	 *
	 * @param model 화면 Model
	 * @return 공개 게시글 목록 View
	 */
	@GetMapping("/")
	public String showPosts(Model model) {
		prepareListModel(model, publicPostService.findAll());
		return LIST_VIEW;
	}

	/**
	 * 지정한 카테고리의 공개 게시글만 표시한다.
	 *
	 * @param categoryId 카테고리 ID
	 * @param model 화면 Model
	 * @return 공개 게시글 목록 View
	 */
	@GetMapping("/categories/{categoryId}")
	public String showPostsByCategory(@PathVariable Long categoryId, Model model) {
		prepareListModel(model, publicPostService.findByCategory(categoryId));
		return LIST_VIEW;
	}

	/**
	 * 지정한 태그의 공개 게시글만 표시한다.
	 *
	 * @param tagId 태그 ID
	 * @param model 화면 Model
	 * @return 공개 게시글 목록 View
	 */
	@GetMapping("/tags/{tagId}")
	public String showPostsByTag(@PathVariable Long tagId, Model model) {
		prepareListModel(model, publicPostService.findByTag(tagId));
		return LIST_VIEW;
	}

	/**
	 * 공개 상태인 게시글의 상세 내용을 표시한다.
	 *
	 * @param postId 게시글 ID
	 * @param model 화면 Model
	 * @return 공개 게시글 상세 View
	 */
	@GetMapping("/posts/{postId}")
	public String showPost(@PathVariable Long postId, Model model) {
		PublicPostResponse.Detail post = publicPostService.findDetail(postId);
		model.addAttribute("pageTitle", post.pageTitle());
		model.addAttribute("publicNavigation", true);
		model.addAttribute("post", post);
		return DETAIL_VIEW;
	}

	/**
	 * 공개 목록 화면 공통 Model을 구성한다.
	 *
	 * @param model 화면 Model
	 * @param listView 공개 목록 화면 정보
	 */
	private void prepareListModel(Model model, PublicPostResponse.ListView listView) {
		model.addAttribute("pageTitle", listView.pageTitle());
		model.addAttribute("publicNavigation", true);
		model.addAttribute("list", listView);
	}
}
