package fullstack.demoproject.devblogcms.post;

import fullstack.demoproject.devblogcms._core.entity.BaseTimeEntity;
import fullstack.demoproject.devblogcms._core.exception.InvalidPostStateException;
import fullstack.demoproject.devblogcms.category.Category;
import fullstack.demoproject.devblogcms.tag.Tag;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 초안과 공개 게시글을 저장한다.
 */
@Getter
@Entity
@Table(
        name = "post",
        indexes = {
                @Index(name = "idx_post_status_created_at", columnList = "status, created_at"),
                @Index(name = "idx_post_category_status_created_at", columnList = "category_id, status, created_at")
        }
)
@NoArgsConstructor
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    // category_id는 null 허용
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    public enum Status {
        DRAFT, PUBLISHED
    }

    private Post(Category category, String title, String content, Status status) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.status = status;
    }

    /**
     * 검증된 입력값으로 게시글 Entity를 생성한다.
     *
     * @param category 카테고리
     * @param title 제목
     * @param content 내용
     * @param status 상태
     * @return 저장 가능한 게시글 Entity
     * @throws IllegalArgumentException 입력값 위반
     */
    public static Post create(@Nullable Category category, String title, String content, Status status) {
        title = title == null ? "" : title.strip();
        if (title.isEmpty() || title.length() > 200) {
            throw new IllegalArgumentException("제목은 1자 이상 200자 이하여야 합니다.");
        }
        content = content == null ? "" : content.strip();
        if (content.isEmpty()) {
            throw new IllegalArgumentException("내용은 필수입력입니다.");
        }
        if (status == null) {
            throw new IllegalArgumentException("상태는 필수입력입니다.");
        }
        return new Post(category, title, content, status);
    }

    /**
     * 게시글의 카테고리, 제목과 본문을 변경한다.
     *
     * @param category 변경할 카테고리
     * @param title 변경할 제목
     * @param content 변경할 본문
     * @throws IllegalArgumentException 제목이나 본문이 유효하지 않을 때
     */
    public void update(@Nullable Category category, String title, String content) {
        title = title == null ? "" : title.strip();
        if (title.isEmpty() || title.length() > 200) {
            throw new IllegalArgumentException("제목은 1자 이상 200자 이하여야 합니다.");
        }
        content = content == null ? "" : content.strip();
        if (content.isEmpty()) {
            throw new IllegalArgumentException("내용은 필수입력입니다.");
        }

        this.category = category;
        this.title = title;
        this.content = content;
    }

    /**
     * 초안 게시글을 공개 상태로 전환한다.
     *
     * @throws InvalidPostStateException 현재 상태가 초안이 아닐 때
     */
    public void publish() {
        if (status != Status.DRAFT) {
            throw new InvalidPostStateException();
        }
        status = Status.PUBLISHED;
    }

    /**
     * 공개 게시글을 초안 상태로 전환한다.
     *
     * @throws InvalidPostStateException 현재 상태가 공개가 아닐 때
     */
    public void draft() {
        if (status != Status.PUBLISHED) {
            throw new InvalidPostStateException();
        }
        status = Status.DRAFT;
    }

    /**
     * 기존 태그 연결을 제거하고 전달받은 태그 목록으로 다시 연결한다.
     *
     * @param tags 새로 연결할 태그 목록
     * @throws IllegalArgumentException 태그가 5개를 초과할 때
     */
    public void replaceTags(List<Tag> tags) {
        if (tags == null || tags.size() > 5) {
            throw new IllegalArgumentException("태그는 최대 5개까지 지정할 수 있습니다.");
        }

        postTags.removeIf(postTag -> tags.stream()
                .noneMatch(tag -> hasSameIdentity(postTag.getTag(), tag)));
        tags.forEach(this::addTag);
    }

    /**
     * 게시글에 태그를 연결한다.
     * 같은 태그가 이미 연결되어 있으면 중복 연결을 만들지 않는다.
     *
     * @param tag 연결할 태그
     * @throws IllegalArgumentException 태그가 없을 때
     */
    public void addTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("태그는 필수입니다.");
        }
        boolean alreadyAdded = postTags.stream()
                .anyMatch(postTag -> hasSameIdentity(postTag.getTag(), tag));
        if (alreadyAdded) {
            return;
        }
        if (postTags.size() >= 5) {
            throw new IllegalArgumentException("태그는 최대 5개까지 지정할 수 있습니다.");
        }
        postTags.add(new PostTag(this, tag));
    }

    /**
     * 게시글에서 지정한 태그 연결을 제거한다.
     * 제거된 연결 엔티티는 orphan removal에 의해 데이터베이스에서도 삭제된다.
     *
     * @param tag 연결을 제거할 태그
     */
    public void removeTag(Tag tag) {
        postTags.removeIf(postTag -> hasSameIdentity(postTag.getTag(), tag));
    }

    /**
     * 게시글에 연결된 태그 관계를 읽기 전용 목록으로 반환한다.
     *
     * @return 수정할 수 없는 게시글-태그 연결 목록
     */
    public List<PostTag> getPostTags() {
        return Collections.unmodifiableList(postTags);
    }

    /**
     * 두 태그가 같은 영속 Entity를 나타내는지 식별자로 비교한다.
     * 아직 저장되지 않아 식별자가 없으면 동일한 객체인지 비교한다.
     *
     * @param existingTag 현재 연결된 태그
     * @param candidateTag 비교할 태그
     * @return 같은 태그이면 true
     */
    private boolean hasSameIdentity(Tag existingTag, Tag candidateTag) {
        if (existingTag == candidateTag) {
            return true;
        }
        if (existingTag == null || candidateTag == null
                || existingTag.getId() == null || candidateTag.getId() == null) {
            return false;
        }
        return existingTag.getId().equals(candidateTag.getId());
    }
}
