package fullstack.demoproject.devblogcms.post;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fullstack.demoproject.devblogcms._core.exception.Exception404;
import fullstack.demoproject.devblogcms._core.exception.InvalidPostStateException;
import fullstack.demoproject.devblogcms.category.Category;
import fullstack.demoproject.devblogcms.category.CategoryRepository;
import fullstack.demoproject.devblogcms.tag.Tag;
import fullstack.demoproject.devblogcms.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 관리자 게시글 조회, 저장, 삭제와 공개 상태 전환 유스케이스를 처리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy. MM. dd.");

	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final TagRepository tagRepository;

	/**
	 * 모든 게시글을 관리자 목록 표시 순서로 조회한다.
	 *
	 * @return 관리자 게시글 목록
	 */
	public List<PostResponse.ListItem> findAll() {
		return postRepository.findAllWithTaxonomiesOrderByCreatedAtDesc().stream()
				.map(post -> new PostResponse.ListItem(
						post.getId(),
						post.getTitle(),
						post.getCategory() == null ? "미지정" : post.getCategory().getName(),
						post.getUpdatedAt().format(DATE_FORMATTER),
						post.getStatus() == Post.Status.PUBLISHED,
						post.getStatus() == Post.Status.DRAFT
				))
				.toList();
	}

	/**
	 * 게시글 수정 Form에 표시할 저장값을 조회한다.
	 *
	 * @param postId 게시글 ID
	 * @return 게시글 수정 Form 값
	 */
	public PostResponse.EditForm findEditForm(Long postId) {
		Post post = postRepository.findDetailById(postId)
				.orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

		Set<Long> tagIds = post.getPostTags().stream()
				.map(PostTag::getTag)
				.map(Tag::getId)
				.collect(Collectors.toCollection(LinkedHashSet::new));

		return new PostResponse.EditForm(
				post.getId(),
				post.getTitle(),
				post.getContent(),
				post.getCategory() == null ? null : post.getCategory().getId(),
				tagIds,
				post.getStatus()
		);
	}

	/**
	 * 현재 선택값이 반영된 카테고리와 태그 선택 항목을 조회한다.
	 *
	 * @param categoryId 선택한 카테고리 ID
	 * @param tagIds 선택한 태그 ID 집합
	 * @return Form 분류 선택 항목
	 */
	public PostResponse.FormOptions findFormOptions(Long categoryId, Set<Long> tagIds) {
		Set<Long> selectedTagIds = tagIds == null ? Set.of() : tagIds;

		List<PostResponse.Option> categories = categoryRepository
				.findAll(Sort.by("name"))
				.stream()
				.map(category -> new PostResponse.Option(
						category.getId(),
						category.getName(),
						Objects.equals(category.getId(), categoryId)
				))
				.toList();

		List<PostResponse.Option> tags = tagRepository
				.findAll(Sort.by("name"))
				.stream()
				.map(tag -> new PostResponse.Option(
						tag.getId(),
						tag.getName(),
						selectedTagIds.contains(tag.getId())
				))
				.toList();

		return new PostResponse.FormOptions(categories, tags);
	}

	/**
	 * 검증된 Form 입력으로 게시글을 생성한다.
	 *
	 * @param request 게시글 저장 입력
	 */
	@Transactional
	public void create(PostRequest.Save request) {
		Category category = findCategory(request.getCategoryId());
		List<Tag> tags = findTags(request.getTagIds());
		Post post = Post.create(
				category,
				request.getTitle(),
				request.getContent(),
				request.getStatus()
		);
		tags.forEach(post::addTag);

		Post savedPost = postRepository.save(post);
		log.info("게시글을 생성했습니다. 게시글 ID={}", savedPost.getId());
	}

	/**
	 * 기존 게시글을 검증된 Form 입력으로 수정한다.
	 *
	 * @param postId 게시글 ID
	 * @param request 게시글 저장 입력
	 */
	@Transactional
	public void update(Long postId, PostRequest.Save request) {
		Post post = postRepository.findDetailById(postId)
				.orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));
		Category category = findCategory(request.getCategoryId());
		List<Tag> tags = findTags(request.getTagIds());

		post.update(category, request.getTitle(), request.getContent());
		post.replaceTags(tags);
		changeStatus(post, request.getStatus());
		log.info("게시글을 수정했습니다. 게시글 ID={}", postId);
	}

	/**
	 * 게시글을 물리 삭제한다.
	 *
	 * @param postId 게시글 ID
	 */
	@Transactional
	public void delete(Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));
		postRepository.delete(post);
		log.info("게시글을 삭제했습니다. 게시글 ID={}", postId);
	}

	/**
	 * 초안 게시글을 공개 상태로 전환한다.
	 *
	 * @param postId 게시글 ID
	 */
	@Transactional
	public void publish(Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));
		post.publish();
		log.info("게시글을 공개 상태로 전환했습니다. 게시글 ID={}", postId);
	}

	/**
	 * 공개 게시글을 초안 상태로 전환한다.
	 *
	 * @param postId 게시글 ID
	 */
	@Transactional
	public void draft(Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));
		post.draft();
		log.info("게시글을 초안 상태로 전환했습니다. 게시글 ID={}", postId);
	}

	/**
	 * 선택한 카테고리를 조회하고 미지정이면 null을 반환한다.
	 *
	 * @param categoryId 선택한 카테고리 ID
	 * @return 선택한 카테고리 또는 null
	 * @throws Exception404 선택한 카테고리가 존재하지 않을 때
	 */
	private Category findCategory(Long categoryId) {
		if (categoryId == null) {
			return null;
		}
		return categoryRepository.findById(categoryId)
				.orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));
	}

	/**
	 * 선택한 태그 ID에 해당하는 태그를 입력 순서대로 조회한다.
	 *
	 * @param tagIds 선택한 태그 ID 집합
	 * @return 선택한 태그 목록
	 * @throws Exception404 선택한 태그 중 존재하지 않는 값이 있을 때
	 */
	private List<Tag> findTags(Set<Long> tagIds) {
		Set<Long> selectedTagIds = tagIds == null ? Set.of() : tagIds;
		Map<Long, Tag> tagsById = tagRepository.findAllById(selectedTagIds).stream()
				.collect(Collectors.toMap(
						Tag::getId,
						tag -> tag,
						(existing, duplicate) -> existing,
						LinkedHashMap::new
				));

		return selectedTagIds.stream()
				.map(tagId -> {
					Tag tag = tagsById.get(tagId);
					if (tag == null) {
						throw new Exception404("태그를 찾을 수 없습니다.");
					}
					return tag;
				})
				.toList();
	}

	/**
	 * 요청 상태가 현재 상태와 다를 때 정의된 공개·초안 전환을 수행한다.
	 *
	 * @param post 상태를 변경할 게시글
	 * @param requestedStatus 요청한 상태
	 */
	private void changeStatus(Post post, Post.Status requestedStatus) {
		if (requestedStatus == null) {
			throw new InvalidPostStateException();
		}
		if (post.getStatus() == requestedStatus) {
			return;
		}
		if (requestedStatus == Post.Status.PUBLISHED) {
			post.publish();
		} else {
			post.draft();
		}
	}
}
