package fullstack.demoproject.devblogcms.post;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

/**
 * 관리자의 게시글 목록, 작성·수정, 삭제와 공개 상태 전환 요청을 처리한다.
 */
@Controller
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {
	private static final String LIST_VIEW = "admin/posts";
	private static final String FORM_VIEW = "admin/post-form";
	private static final String LIST_REDIRECT = "redirect:/admin/posts";

	private final PostService postService;

	/**
	 * 초안과 공개 게시글을 모두 표시한다.
	 *
	 * @param model 화면 Model
	 * @return 관리자 게시글 목록 View
	 */
	@GetMapping
	public String showPosts(Model model) {
		var posts = postService.findAll();
		model.addAttribute("pageTitle", "게시글 관리");
		model.addAttribute("posts", posts);
		model.addAttribute("hasPosts", !posts.isEmpty());
		return LIST_VIEW;
	}

	/**
	 * 빈 게시글 작성 Form과 분류 선택 항목을 표시한다.
	 *
	 * @param model 화면 Model
	 * @return 게시글 작성 View
	 */
	@GetMapping("/new")
	public String showCreateForm(Model model) {
		prepareFormModel(model, new PostRequest.Save(), false, null);
		return FORM_VIEW;
	}

	/**
	 * Form 입력을 검증하고 게시글을 생성한다.
	 *
	 * @param postRequest 게시글 저장 입력
	 * @param model 화면 Model
	 * @param redirectAttributes 다음 요청에 전달할 메시지
	 * @return 검증 실패 시 작성 View, 성공 시 목록 Redirect
	 */
	@PostMapping
	public String create(
			PostRequest.Save postRequest,
			Model model,
			RedirectAttributes redirectAttributes
	) {
		if (!postRequest.validate()) {
			prepareFormModel(model, postRequest, false, null);
			return FORM_VIEW;
		}

		postService.create(postRequest);
		redirectAttributes.addFlashAttribute("successMessage", saveSuccessMessage(postRequest.getStatus()));
		return LIST_REDIRECT;
	}

	/**
	 * 저장된 게시글 값과 분류 선택 항목을 수정 Form에 표시한다.
	 *
	 * @param postId 게시글 ID
	 * @param model 화면 Model
	 * @return 게시글 수정 View
	 */
	@GetMapping("/{postId}/edit")
	public String showEditForm(@PathVariable Long postId, Model model) {
		PostResponse.EditForm editForm = postService.findEditForm(postId);
		PostRequest.Save postRequest = toRequest(editForm);
		prepareFormModel(model, postRequest, true, postId);
		return FORM_VIEW;
	}

	/**
	 * Form 입력을 검증하고 기존 게시글을 수정한다.
	 *
	 * @param postId 게시글 ID
	 * @param postRequest 게시글 저장 입력
	 * @param model 화면 Model
	 * @param redirectAttributes 다음 요청에 전달할 메시지
	 * @return 검증 실패 시 수정 View, 성공 시 목록 Redirect
	 */
	@PostMapping("/{postId}/edit")
	public String update(
			@PathVariable Long postId,
			PostRequest.Save postRequest,
			Model model,
			RedirectAttributes redirectAttributes
	) {
		if (!postRequest.validate()) {
			prepareFormModel(model, postRequest, true, postId);
			return FORM_VIEW;
		}

		postService.update(postId, postRequest);
		redirectAttributes.addFlashAttribute("successMessage", "게시글을 수정했습니다.");
		return LIST_REDIRECT;
	}

	/**
	 * 게시글을 물리 삭제한다.
	 *
	 * @param postId 게시글 ID
	 * @param redirectAttributes 다음 요청에 전달할 메시지
	 * @return 관리자 게시글 목록 Redirect
	 */
	@PostMapping("/{postId}/delete")
	public String delete(
			@PathVariable Long postId,
			RedirectAttributes redirectAttributes
	) {
		postService.delete(postId);
		redirectAttributes.addFlashAttribute("successMessage", "게시글을 삭제했습니다.");
		return LIST_REDIRECT;
	}

	/**
	 * 초안 게시글을 공개 상태로 전환한다.
	 *
	 * @param postId 게시글 ID
	 * @param redirectAttributes 다음 요청에 전달할 메시지
	 * @return 관리자 게시글 목록 Redirect
	 */
	@PostMapping("/{postId}/publish")
	public String publish(
			@PathVariable Long postId,
			RedirectAttributes redirectAttributes
	) {
		postService.publish(postId);
		redirectAttributes.addFlashAttribute("successMessage", "게시글을 공개했습니다.");
		return LIST_REDIRECT;
	}

	/**
	 * 공개 게시글을 초안 상태로 전환한다.
	 *
	 * @param postId 게시글 ID
	 * @param redirectAttributes 다음 요청에 전달할 메시지
	 * @return 관리자 게시글 목록 Redirect
	 */
	@PostMapping("/{postId}/draft")
	public String draft(
			@PathVariable Long postId,
			RedirectAttributes redirectAttributes
	) {
		postService.draft(postId);
		redirectAttributes.addFlashAttribute("successMessage", "게시글을 초안으로 전환했습니다.");
		return LIST_REDIRECT;
	}

	/**
	 * 게시글 Form 입력, 분류 선택 항목과 검증 오류를 화면 Model에 담는다.
	 *
	 * @param model 화면 Model
	 * @param postRequest 게시글 Form 입력
	 * @param editMode 수정 화면 여부
	 * @param postId 수정할 게시글 ID
	 */
	private void prepareFormModel(
			Model model,
			PostRequest.Save postRequest,
			boolean editMode,
			Long postId
	) {
		PostResponse.FormOptions options = postService.findFormOptions(
				postRequest.getCategoryId(),
				postRequest.getTagIds()
		);
		model.addAttribute("pageTitle", editMode ? "게시글 수정" : "게시글 작성");
		model.addAttribute("formTitle", editMode ? "게시글 수정" : "게시글 작성");
		model.addAttribute("formAction", editMode ? "/admin/posts/" + postId + "/edit" : "/admin/posts");
		model.addAttribute("isEdit", editMode);
		model.addAttribute("postId", postId);
		model.addAttribute("postRequest", postRequest);
		model.addAttribute("categories", options.categories());
		model.addAttribute("tags", options.tags());
		model.addAttribute("titleError", postRequest.getTitleError());
		model.addAttribute("hasTitleError", !postRequest.getTitleError().isEmpty());
		model.addAttribute("contentError", postRequest.getContentError());
		model.addAttribute("hasContentError", !postRequest.getContentError().isEmpty());
		model.addAttribute("categoryIdError", postRequest.getCategoryIdError());
		model.addAttribute("hasCategoryIdError", !postRequest.getCategoryIdError().isEmpty());
		model.addAttribute("tagIdsError", postRequest.getTagIdsError());
		model.addAttribute("hasTagIdsError", !postRequest.getTagIdsError().isEmpty());
		if (!postRequest.getStatusError().isEmpty()) {
			model.addAttribute("errorMessage", postRequest.getStatusError());
		}
	}

	/**
	 * 수정 화면 조회 결과를 다시 전송 가능한 Form 입력 객체로 변환한다.
	 *
	 * @param editForm 저장된 게시글 값
	 * @return 수정 Form 입력 객체
	 */
	private PostRequest.Save toRequest(PostResponse.EditForm editForm) {
		PostRequest.Save request = new PostRequest.Save();
		request.setTitle(editForm.title());
		request.setContent(editForm.content());
		request.setCategoryId(editForm.categoryId());
		request.setTagIds(editForm.tagIds());
		request.setStatus(editForm.status());
		return request;
	}

	/**
	 * 생성된 게시글 상태에 맞는 성공 메시지를 반환한다.
	 *
	 * @param status 저장한 게시글 상태
	 * @return 사용자에게 표시할 성공 메시지
	 */
	private String saveSuccessMessage(Post.Status status) {
		return status == Post.Status.PUBLISHED
				? "게시글을 공개했습니다."
				: "게시글을 초안으로 저장했습니다.";
	}
}
