package fullstack.demoproject.devblogcms.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fullstack.demoproject.devblogcms.tag.Tag;

class PostTagTest {

    @Test
    @DisplayName("게시글에 태그를 추가하면 중간 엔티티가 양쪽 엔티티를 연결한다")
    void addTag_validTag_createsPostTag() {
        Post post = Post.create(null, "JPA 연관관계", "중간 엔티티를 설명합니다.", Post.Status.DRAFT);
        Tag tag = Tag.create("JPA");

        post.addTag(tag);

        assertThat(post.getPostTags()).hasSize(1);
        assertThat(post.getPostTags().getFirst().getPost()).isSameAs(post);
        assertThat(post.getPostTags().getFirst().getTag()).isSameAs(tag);
    }

    @Test
    @DisplayName("같은 태그를 두 번 추가해도 연결은 하나만 유지한다")
    void addTag_sameTagTwice_keepsSinglePostTag() {
        Post post = Post.create(null, "JPA 연관관계", "중간 엔티티를 설명합니다.", Post.Status.DRAFT);
        Tag tag = Tag.create("JPA");

        post.addTag(tag);
        post.addTag(tag);

        assertThat(post.getPostTags()).hasSize(1);
    }

    @Test
    @DisplayName("게시글에는 태그를 최대 5개까지만 연결할 수 있다")
    void addTag_sixthTag_throwsException() {
        Post post = Post.create(null, "JPA 연관관계", "중간 엔티티를 설명합니다.", Post.Status.DRAFT);
        for (int index = 1; index <= 5; index++) {
            post.addTag(Tag.create("태그" + index));
        }

        assertThatThrownBy(() -> post.addTag(Tag.create("태그6")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("태그는 최대 5개까지 지정할 수 있습니다.");
        assertThat(post.getPostTags()).hasSize(5);
    }

    @Test
    @DisplayName("태그 연결을 제거하면 게시글의 중간 엔티티 목록에서도 제거된다")
    void removeTag_linkedTag_removesPostTag() {
        Post post = Post.create(null, "JPA 연관관계", "중간 엔티티를 설명합니다.", Post.Status.DRAFT);
        Tag tag = Tag.create("JPA");
        post.addTag(tag);

        post.removeTag(tag);

        assertThat(post.getPostTags()).isEmpty();
    }
}
