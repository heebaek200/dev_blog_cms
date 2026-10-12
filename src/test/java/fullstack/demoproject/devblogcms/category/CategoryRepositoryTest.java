package fullstack.demoproject.devblogcms.category;

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
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 이름은 앞뒤 공백을 제거하고 영문 소문자로 정규화한다")
    void create_nameWithSpaces_normalizesName() {
        Category category = Category.create("  Spring Boot  ");

        assertThat(category.getName()).isEqualTo("Spring Boot");
        assertThat(category.getNormalizedName()).isEqualTo("spring boot");
    }

    @Test
    @DisplayName("카테고리를 저장하면 감사 시각과 정규화 이름 중복 여부를 조회할 수 있다")
    void save_validCategory_recordsAuditTimestampsAndExists() {
        Category savedCategory = categoryRepository.saveAndFlush(Category.create("Spring"));

        assertThat(savedCategory.getCreatedAt()).isNotNull();
        assertThat(savedCategory.getUpdatedAt()).isNotNull();
        assertThat(categoryRepository.existsByNormalizedName("spring")).isTrue();
        assertThat(categoryRepository.existsByNormalizedName("java")).isFalse();
    }

    @Test
    @DisplayName("정규화한 이름이 같은 카테고리는 중복 저장할 수 없다")
    void save_sameNormalizedName_throwsException() {
        categoryRepository.saveAndFlush(Category.create("Spring"));

        assertThatThrownBy(() -> categoryRepository.saveAndFlush(Category.create("  SPRING  ")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
