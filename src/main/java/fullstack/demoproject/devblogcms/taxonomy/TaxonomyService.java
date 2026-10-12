package fullstack.demoproject.devblogcms.taxonomy;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fullstack.demoproject.devblogcms._core.exception.DuplicateTaxonomyException;
import fullstack.demoproject.devblogcms._core.exception.Exception404;
import fullstack.demoproject.devblogcms._core.exception.TaxonomyInUseException;
import fullstack.demoproject.devblogcms.category.Category;
import fullstack.demoproject.devblogcms.category.CategoryRepository;
import fullstack.demoproject.devblogcms.tag.Tag;
import fullstack.demoproject.devblogcms.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 카테고리와 태그의 목록 조회, 생성과 삭제 업무 규칙을 처리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxonomyService {

	private final CategoryRepository categoryRepository;
	private final TagRepository tagRepository;

	/**
	 * 카테고리와 태그를 사용 게시글 수와 함께 조회한다.
	 *
	 * @return 관리 화면에 표시할 분류 목록
	 */
	public TaxonomyResponse.Management findAll() {
		return new TaxonomyResponse.Management(
				categoryRepository.findAllWithPostCount().stream()
						.map(category -> new TaxonomyResponse.Item(
								category.getId(),
								category.getName(),
								category.getPostCount()
						))
						.toList(),
				tagRepository.findAllWithPostCount().stream()
						.map(tag -> new TaxonomyResponse.Item(
								tag.getId(),
								tag.getName(),
								tag.getPostCount()
						))
						.toList()
		);
	}

	/**
	 * 정규화한 이름이 중복되지 않는 카테고리를 생성한다.
	 *
	 * @param name 카테고리 이름
	 * @throws DuplicateTaxonomyException 같은 정규화 이름이 이미 존재할 때
	 */
	@Transactional
	public void createCategory(String name) {
		Category category = Category.create(name);
		if (categoryRepository.existsByNormalizedName(category.getNormalizedName())) {
			log.warn("중복된 카테고리 생성을 거부했습니다.");
			throw DuplicateTaxonomyException.forCategory();
		}

		try {
			Category savedCategory = categoryRepository.saveAndFlush(category);
			log.info("카테고리를 생성했습니다. 카테고리 ID={}", savedCategory.getId());
		} catch (DataIntegrityViolationException exception) {
			log.warn("데이터베이스 제약조건에 따라 중복된 카테고리 생성을 거부했습니다.");
			throw DuplicateTaxonomyException.forCategory();
		}
	}

	/**
	 * 정규화한 이름이 중복되지 않는 태그를 생성한다.
	 *
	 * @param name 태그 이름
	 * @throws DuplicateTaxonomyException 같은 정규화 이름이 이미 존재할 때
	 */
	@Transactional
	public void createTag(String name) {
		Tag tag = Tag.create(name);
		if (tagRepository.existsByNormalizedName(tag.getNormalizedName())) {
			log.warn("중복된 태그 생성을 거부했습니다.");
			throw DuplicateTaxonomyException.forTag();
		}

		try {
			Tag savedTag = tagRepository.saveAndFlush(tag);
			log.info("태그를 생성했습니다. 태그 ID={}", savedTag.getId());
		} catch (DataIntegrityViolationException exception) {
			log.warn("데이터베이스 제약조건에 따라 중복된 태그 생성을 거부했습니다.");
			throw DuplicateTaxonomyException.forTag();
		}
	}

	/**
	 * 게시글에서 사용하지 않는 카테고리를 삭제한다.
	 *
	 * @param categoryId 삭제할 카테고리 ID
	 * @throws Exception404 카테고리가 존재하지 않을 때
	 * @throws TaxonomyInUseException 카테고리를 사용하는 게시글이 있을 때
	 */
	@Transactional
	public void deleteCategory(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));
		if (categoryRepository.countPostsById(categoryId) > 0) {
			log.warn("사용 중인 카테고리 삭제를 거부했습니다. 카테고리 ID={}", categoryId);
			throw new TaxonomyInUseException();
		}

		try {
			categoryRepository.delete(category);
			categoryRepository.flush();
			log.info("카테고리를 삭제했습니다. 카테고리 ID={}", categoryId);
		} catch (DataIntegrityViolationException exception) {
			log.warn("데이터베이스 제약조건에 따라 사용 중인 카테고리 삭제를 거부했습니다. 카테고리 ID={}", categoryId);
			throw new TaxonomyInUseException();
		}
	}

	/**
	 * 게시글에서 사용하지 않는 태그를 삭제한다.
	 *
	 * @param tagId 삭제할 태그 ID
	 * @throws Exception404 태그가 존재하지 않을 때
	 * @throws TaxonomyInUseException 태그를 사용하는 게시글이 있을 때
	 */
	@Transactional
	public void deleteTag(Long tagId) {
		Tag tag = tagRepository.findById(tagId)
				.orElseThrow(() -> new Exception404("태그를 찾을 수 없습니다."));
		if (tagRepository.countPostsById(tagId) > 0) {
			log.warn("사용 중인 태그 삭제를 거부했습니다. 태그 ID={}", tagId);
			throw new TaxonomyInUseException();
		}

		try {
			tagRepository.delete(tag);
			tagRepository.flush();
			log.info("태그를 삭제했습니다. 태그 ID={}", tagId);
		} catch (DataIntegrityViolationException exception) {
			log.warn("데이터베이스 제약조건에 따라 사용 중인 태그 삭제를 거부했습니다. 태그 ID={}", tagId);
			throw new TaxonomyInUseException();
		}
	}
}
