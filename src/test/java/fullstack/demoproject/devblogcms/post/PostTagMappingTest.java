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

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class PostTagMappingTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

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
}
