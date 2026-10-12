package fullstack.demoproject.devblogcms.post;

import fullstack.demoproject.devblogcms._core.entity.BaseTimeEntity;
import fullstack.demoproject.devblogcms.category.Category;
import fullstack.demoproject.devblogcms.tag.Tag;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
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

    @Builder
    public Post(Category category, String title, String content, Status status) {
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
                .anyMatch(postTag -> postTag.getTag() == tag);
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
        postTags.removeIf(postTag -> postTag.getTag() == tag);
    }

    /**
     * 게시글에 연결된 태그 관계를 읽기 전용 목록으로 반환한다.
     *
     * @return 수정할 수 없는 게시글-태그 연결 목록
     */
    public List<PostTag> getPostTags() {
        return Collections.unmodifiableList(postTags);
    }
}
