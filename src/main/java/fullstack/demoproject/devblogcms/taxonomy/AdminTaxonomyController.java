package fullstack.demoproject.devblogcms.taxonomy;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

/**
 * 관리자의 카테고리·태그 목록, 생성과 삭제 요청을 처리한다.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminTaxonomyController {

	private static final String MANAGEMENT_VIEW = "admin/taxonomies";
	private static final String MANAGEMENT_REDIRECT = "redirect:/admin/taxonomies";

	private final TaxonomyService taxonomyService;

	/**
	 * 카테고리와 태그를 사용 게시글 수와 함께 표시한다.
	 *
	 * @param model 화면 Model
	 * @return 카테고리·태그 관리 View
	 */
	@GetMapping("/taxonomies")
	public String showTaxonomies(Model model) {
		prepareModel(model, new TaxonomyRequest.Create(), new TaxonomyRequest.Create());
		return MANAGEMENT_VIEW;
	}

	/**
	 * 카테고리 이름을 검증하고 새 카테고리를 생성한다.
	 *
	 * @param categoryRequest 카테고리 생성 Form 입력
	 * @param model 화면 Model
	 * @param redirectAttributes 다음 요청에 전달할 메시지
	 * @return 검증 실패 시 관리 View, 성공 시 관리 화면 Redirect
	 */
	@PostMapping("/categories")
	public String createCategory(
			TaxonomyRequest.Create categoryRequest,
			Model model,
			RedirectAttributes redirectAttributes
	) {
		if (!categoryRequest.validate()) {
			prepareModel(model, categoryRequest, new TaxonomyRequest.Create());
			return MANAGEMENT_VIEW;
		}

		taxonomyService.createCategory(categoryRequest.getName());
		redirectAttributes.addFlashAttribute("successMessage", "카테고리를 추가했습니다.");
		return MANAGEMENT_REDIRECT;
	}

	/**
	 * 게시글에서 사용하지 않는 카테고리를 삭제한다.
	 *
	 * @param categoryId 삭제할 카테고리 ID
	 * @param redirectAttributes 다음 요청에 전달할 메시지
	 * @return 관리 화면 Redirect
	 */
	@PostMapping("/categories/{categoryId}/delete")
	public String deleteCategory(
			@PathVariable Long categoryId,
			RedirectAttributes redirectAttributes
	) {
		taxonomyService.deleteCategory(categoryId);
		redirectAttributes.addFlashAttribute("successMessage", "카테고리를 삭제했습니다.");
		return MANAGEMENT_REDIRECT;
	}

	/**
	 * 태그 이름을 검증하고 새 태그를 생성한다.
	 *
	 * @param tagRequest 태그 생성 Form 입력
	 * @param model 화면 Model
	 * @param redirectAttributes 다음 요청에 전달할 메시지
	 * @return 검증 실패 시 관리 View, 성공 시 관리 화면 Redirect
	 */
	@PostMapping("/tags")
	public String createTag(
			TaxonomyRequest.Create tagRequest,
			Model model,
			RedirectAttributes redirectAttributes
	) {
		if (!tagRequest.validate()) {
			prepareModel(model, new TaxonomyRequest.Create(), tagRequest);
			return MANAGEMENT_VIEW;
		}

		taxonomyService.createTag(tagRequest.getName());
		redirectAttributes.addFlashAttribute("successMessage", "태그를 추가했습니다.");
		return MANAGEMENT_REDIRECT;
	}

	/**
	 * 게시글에서 사용하지 않는 태그를 삭제한다.
	 *
	 * @param tagId 삭제할 태그 ID
	 * @param redirectAttributes 다음 요청에 전달할 메시지
	 * @return 관리 화면 Redirect
	 */
	@PostMapping("/tags/{tagId}/delete")
	public String deleteTag(
			@PathVariable Long tagId,
			RedirectAttributes redirectAttributes
	) {
		taxonomyService.deleteTag(tagId);
		redirectAttributes.addFlashAttribute("successMessage", "태그를 삭제했습니다.");
		return MANAGEMENT_REDIRECT;
	}

	/**
	 * 분류 목록, 생성 Form 입력과 검증 오류를 관리 화면 Model에 담는다.
	 *
	 * @param model 화면 Model
	 * @param categoryRequest 카테고리 생성 Form
	 * @param tagRequest 태그 생성 Form
	 */
	private void prepareModel(
			Model model,
			TaxonomyRequest.Create categoryRequest,
			TaxonomyRequest.Create tagRequest
	) {
		TaxonomyResponse.Management management = taxonomyService.findAll();
		model.addAttribute("pageTitle", "카테고리·태그 관리");
		model.addAttribute("categories", management.categories());
		model.addAttribute("tags", management.tags());
		model.addAttribute("categoryRequest", categoryRequest);
		model.addAttribute("categoryNameError", categoryRequest.getNameError());
		model.addAttribute("hasCategoryNameError", !categoryRequest.getNameError().isEmpty());
		model.addAttribute("tagRequest", tagRequest);
		model.addAttribute("tagNameError", tagRequest.getNameError());
		model.addAttribute("hasTagNameError", !tagRequest.getNameError().isEmpty());
	}
}
