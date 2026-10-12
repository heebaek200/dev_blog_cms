package fullstack.demoproject.devblogcms.post;

import java.io.Serializable;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import fullstack.demoproject.devblogcms.tag.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글과 태그의 다대다 관계를 연결하는 중간 엔티티다.
 */
@Getter
@Entity
@Table(
        name = "post_tag",
        indexes = @Index(name = "idx_post_tag_tag_post", columnList = "tag_id, post_id")
)
@NoArgsConstructor
public class PostTag {

    @EmbeddedId
    private PostTagId id = new PostTagId();

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    /**
     * 게시글과 태그를 연결하는 엔티티를 생성한다.
     * 복합키 값은 두 연관 엔티티가 영속화될 때 {@link MapsId}로 채워진다.
     *
     * @param post 연결할 게시글
     * @param tag 연결할 태그
     * @throws IllegalArgumentException 게시글이나 태그가 없을 때
     */
    PostTag(Post post, Tag tag) {
        if (post == null || tag == null) {
            throw new IllegalArgumentException("게시글과 태그는 필수입니다.");
        }
        this.post = post;
        this.tag = tag;
    }

    /**
     * 게시글 ID와 태그 ID로 연결 행을 식별하는 복합키다.
     */
    @Getter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostTagId implements Serializable {

        @Column(name = "post_id")
        private Long postId;

        @Column(name = "tag_id")
        private Long tagId;
    }
}
