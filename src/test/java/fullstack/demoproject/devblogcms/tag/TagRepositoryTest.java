package fullstack.demoproject.devblogcms.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import fullstack.demoproject.devblogcms._core.config.JpaConfig;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    @DisplayName("태그 이름은 앞뒤 공백을 제거하고 영문 소문자로 정규화한다")
    void create_nameWithSpaces_normalizesName() {
        Tag tag = Tag.create("  Spring Data  ");

        assertThat(tag.getName()).isEqualTo("Spring Data");
        assertThat(tag.getNormalizedName()).isEqualTo("spring data");
    }

    @Test
    @DisplayName("태그를 저장하면 감사 시각과 정규화 이름 중복 여부를 조회할 수 있다")
    void save_validTag_recordsAuditTimestampsAndExists() {
        Tag savedTag = tagRepository.saveAndFlush(Tag.create("JPA"));

        assertThat(savedTag.getCreatedAt()).isNotNull();
        assertThat(savedTag.getUpdatedAt()).isNotNull();
        assertThat(tagRepository.existsByNormalizedName("jpa")).isTrue();
        assertThat(tagRepository.existsByNormalizedName("java")).isFalse();
    }

    @Test
    @DisplayName("정규화한 이름이 같은 태그는 중복 저장할 수 없다")
    void save_sameNormalizedName_throwsException() {
        tagRepository.saveAndFlush(Tag.create("JPA"));

        assertThatThrownBy(() -> tagRepository.saveAndFlush(Tag.create("  jpa  ")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
