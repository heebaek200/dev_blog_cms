package fullstack.demoproject.devblogcms.post;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fullstack.demoproject.devblogcms._core.exception.Exception404;
import fullstack.demoproject.devblogcms.category.Category;
import fullstack.demoproject.devblogcms.category.CategoryRepository;
import fullstack.demoproject.devblogcms.tag.Tag;
import fullstack.demoproject.devblogcms.tag.TagRepository;
import lombok.RequiredArgsConstructor;

/**
 * 공개된 게시글의 목록·상세 조회와 카테고리·태그 필터를 처리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicPostService {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy. MM. dd.");

	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final TagRepository tagRepository;

	/**
	 * 모든 공개 게시글을 최근 작성 순으로 조회한다.
	 *
	 * @return 전체 공개 게시글 목록 화면 정보
	 */
	public PublicPostResponse.ListView findAll() {
		List<Post> posts = postRepository.findAllByStatusWithTaxonomiesOrderByCreatedAtDesc(
				Post.Status.PUBLISHED
		);
		return createListView(posts, null, null);
	}

	/**
	 * 지정한 카테고리의 공개 게시글을 최근 작성 순으로 조회한다.
	 *
	 * @param categoryId 카테고리 ID
	 * @return 카테고리별 공개 게시글 목록 화면 정보
	 * @throws Exception404 카테고리가 존재하지 않을 때
	 */
	public PublicPostResponse.ListView findByCategory(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));
		List<Post> posts = postRepository
				.findAllByCategoryIdAndStatusWithTaxonomiesOrderByCreatedAtDesc(
						categoryId,
						Post.Status.PUBLISHED
				);
		return createListView(posts, category, null);
	}

	/**
	 * 지정한 태그의 공개 게시글을 최근 작성 순으로 조회한다.
	 *
	 * @param tagId 태그 ID
	 * @return 태그별 공개 게시글 목록 화면 정보
	 * @throws Exception404 태그가 존재하지 않을 때
	 */
	public PublicPostResponse.ListView findByTag(Long tagId) {
		Tag tag = tagRepository.findById(tagId)
				.orElseThrow(() -> new Exception404("태그를 찾을 수 없습니다."));
		List<Post> posts = postRepository.findAllByTagIdAndStatusWithTaxonomiesOrderByCreatedAtDesc(
				tagId,
				Post.Status.PUBLISHED
		);
		return createListView(posts, null, tag);
	}

	/**
	 * 공개 상태인 게시글의 상세 정보를 조회한다.
	 * 초안과 존재하지 않는 게시글은 같은 404로 처리한다.
	 *
	 * @param postId 게시글 ID
	 * @return 공개 게시글 상세 화면 정보
	 * @throws Exception404 공개 상태인 게시글이 존재하지 않을 때
	 */
	public PublicPostResponse.Detail findDetail(Long postId) {
		Post post = postRepository.findDetailByIdAndStatus(postId, Post.Status.PUBLISHED)
				.orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));
		List<PublicPostResponse.TagLink> tags = createTagLinks(post);
		return new PublicPostResponse.Detail(
				post.getTitle(),
				post.getTitle(),
				post.getContent(),
				post.getCreatedAt().format(DATE_FORMATTER),
				createCategoryLink(post.getCategory()),
				tags,
				!tags.isEmpty()
		);
	}

	/**
	 * 조회한 게시글과 현재 필터를 공개 목록 화면 정보로 변환한다.
	 *
	 * @param posts 공개 게시글 목록
	 * @param selectedCategory 선택한 카테고리 또는 null
	 * @param selectedTag 선택한 태그 또는 null
	 * @return 공개 목록 화면 정보
	 */
	private PublicPostResponse.ListView createListView(
			List<Post> posts,
			Category selectedCategory,
			Tag selectedTag
	) {
		String listLabel = "Latest";
		String listTitle = "최근 글";
		if (selectedCategory != null) {
			listLabel = "Category";
			listTitle = selectedCategory.getName();
		} else if (selectedTag != null) {
			listLabel = "Tag";
			listTitle = "#" + selectedTag.getName();
		}

		List<PublicPostResponse.ListItem> postItems = posts.stream()
				.map(this::createListItem)
				.toList();
		return new PublicPostResponse.ListView(
				listTitle,
				listLabel,
				listTitle,
				postItems.size() + "개의 글",
				selectedCategory == null && selectedTag == null
						? "아직 공개된 글이 없습니다."
						: "선택한 조건의 글이 없습니다.",
				selectedCategory == null && selectedTag == null
						? "새로운 글이 공개되면 이곳에서 확인할 수 있습니다."
						: "다른 카테고리나 태그를 선택해 주세요.",
				selectedCategory == null && selectedTag == null,
				createCategoryOptions(selectedCategory),
				createTagOptions(selectedTag),
				postItems,
				!postItems.isEmpty()
		);
	}

	/**
	 * 게시글 Entity를 공개 목록 항목으로 변환한다.
	 *
	 * @param post 분류 정보까지 함께 조회한 게시글
	 * @return 공개 목록 항목
	 */
	private PublicPostResponse.ListItem createListItem(Post post) {
		List<PublicPostResponse.TagLink> tags = createTagLinks(post);
		return new PublicPostResponse.ListItem(
				post.getId(),
				post.getTitle(),
				post.getCreatedAt().format(DATE_FORMATTER),
				createCategoryLink(post.getCategory()),
				tags,
				!tags.isEmpty()
		);
	}

	/**
	 * 전체 카테고리를 이름순 필터 Link로 변환한다.
	 *
	 * @param selectedCategory 현재 선택한 카테고리 또는 null
	 * @return 카테고리 필터 목록
	 */
	private List<PublicPostResponse.FilterOption> createCategoryOptions(Category selectedCategory) {
		return categoryRepository.findAll(Sort.by("name")).stream()
				.map(category -> new PublicPostResponse.FilterOption(
						category.getName(),
						"/categories/" + category.getId(),
						selectedCategory != null && category.getId().equals(selectedCategory.getId())
				))
				.toList();
	}

	/**
	 * 전체 태그를 이름순 필터 Link로 변환한다.
	 *
	 * @param selectedTag 현재 선택한 태그 또는 null
	 * @return 태그 필터 목록
	 */
	private List<PublicPostResponse.FilterOption> createTagOptions(Tag selectedTag) {
		return tagRepository.findAll(Sort.by("name")).stream()
				.map(tag -> new PublicPostResponse.FilterOption(
						"#" + tag.getName(),
						"/tags/" + tag.getId(),
						selectedTag != null && tag.getId().equals(selectedTag.getId())
				))
				.toList();
	}

	/**
	 * 카테고리를 공개 필터 Link로 변환한다.
	 *
	 * @param category 게시글 카테고리 또는 null
	 * @return 카테고리 Link 또는 null
	 */
	private PublicPostResponse.CategoryLink createCategoryLink(Category category) {
		if (category == null) {
			return null;
		}
		return new PublicPostResponse.CategoryLink(
				category.getName(),
				"/categories/" + category.getId()
		);
	}

	/**
	 * 게시글 태그를 이름순 공개 필터 Link로 변환한다.
	 *
	 * @param post 분류 정보까지 함께 조회한 게시글
	 * @return 태그 Link 목록
	 */
	private List<PublicPostResponse.TagLink> createTagLinks(Post post) {
		return post.getPostTags().stream()
				.map(PostTag::getTag)
				.sorted(Comparator.comparing(Tag::getName))
				.map(tag -> new PublicPostResponse.TagLink(
						tag.getName(),
						"/tags/" + tag.getId()
				))
				.toList();
	}
}
