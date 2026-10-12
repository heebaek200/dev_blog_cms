package fullstack.demoproject.devblogcms.post;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import fullstack.demoproject.devblogcms._core.config.JpaConfig;
import fullstack.demoproject.devblogcms.tag.Tag;
import fullstack.demoproject.devblogcms.tag.TagRepository;
import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class PostTagMappingTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("게시글을 저장하면 태그 연결과 복합키가 함께 저장된다")
    void persist_postWithTag_savesJoinEntityAndCompositeId() {
        Tag tag = tagRepository.saveAndFlush(Tag.create("JPA"));

        Post post = Post.create(null, "JPA 연관관계", "중간 엔티티를 설명합니다.", Post.Status.DRAFT);
        post.addTag(tag);
        Post savedPost = postRepository.saveAndFlush(post);

        PostTag postTag = savedPost.getPostTags().getFirst();
        assertThat(postTag.getId().getPostId()).isEqualTo(savedPost.getId());
        assertThat(postTag.getId().getTagId()).isEqualTo(tag.getId());
        assertThat(postRepository.findDetailById(savedPost.getId())).isPresent();
    }

    @Test
    @DisplayName("서로 다른 Entity 인스턴스라도 같은 태그 ID이면 중복 연결과 제거에 동일하게 사용한다")
    void tagIdentity_samePersistentId_preventsDuplicateAndRemovesLink() {
        Tag detachedTag = tagRepository.saveAndFlush(Tag.create("JPA"));
        entityManager.detach(detachedTag);
        Tag managedTag = tagRepository.findById(detachedTag.getId()).orElseThrow();
        Post post = Post.create(null, "태그 식별자 비교", "태그 ID로 동일성을 판단합니다.", Post.Status.DRAFT);

        post.addTag(detachedTag);
        post.addTag(managedTag);

        assertThat(detachedTag).isNotSameAs(managedTag);
        assertThat(post.getPostTags()).hasSize(1);

        post.removeTag(managedTag);

        assertThat(post.getPostTags()).isEmpty();
    }
}
