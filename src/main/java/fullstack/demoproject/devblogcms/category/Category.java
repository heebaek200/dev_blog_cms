package fullstack.demoproject.devblogcms.category;

import fullstack.demoproject.devblogcms._core.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Locale;

/**
 * 게시글을 선택적으로 분류하는 카테고리를 저장한다.
 */
@Getter
@Entity
@Table(name = "category")
@NoArgsConstructor
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String normalizedName;

    public Category(String name, String normalizedName) {
        this.name = name;
        this.normalizedName = normalizedName;
    }

    /**
     * 카테고리 Entity를 생성한다.
     * Category 이름은 Trim한 정규화 값으로 중복을 방지한다.
     *
     * @param name 카테고리명
     * @return 저장 가능한 카테고리 Entity
     * @throws IllegalArgumentException 카테고리명이 비어 있거나 50자를 초과할 때
     */
    public static Category create(String name) {
        name = name == null ? "" : name.strip();
        if (name.isEmpty() || name.length() > 50) {
            throw new IllegalArgumentException("카테고리 이름은 1자 이상 50자 이하여야 합니다.");
        }
        String normalizedName = name.toLowerCase(Locale.ROOT);
        return new Category(name, normalizedName);
    }
}
