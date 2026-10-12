package fullstack.demoproject.devblogcms.tag;

import fullstack.demoproject.devblogcms._core.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Getter
@Entity
@Table(name = "tag")
@NoArgsConstructor
public class Tag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String normalizedName;

    private Tag(String name, String normalizedName) {
        this.name = name;
        this.normalizedName = normalizedName;
    }

    /**
     * 태그 Entity를 생성한다.
     * 태그 이름은 Trim한 정규화 값으로 중복을 방지한다.
     *
     * @param name 태그명
     * @return 저장 가능한 태그 Entity
     * @throws IllegalArgumentException 태그명이 비어 있거나 50자를 초과할 때
     */
    public static Tag create(String name) {
        name = name == null ? "" : name.strip();
        if (name.isEmpty() || name.length() > 50) {
            throw new IllegalArgumentException("태그 이름은 1자 이상 50자 이하여야 합니다.");
        }
        String normalizedName = name.toLowerCase(Locale.ROOT);
        return new Tag(name, normalizedName);
    }
}
