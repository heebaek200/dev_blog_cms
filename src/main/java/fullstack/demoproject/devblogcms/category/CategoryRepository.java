package fullstack.demoproject.devblogcms.category;


import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 카테고리 CRUD를 담당한다.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 카테고리명(정규화) 중복 체크
     *
     * @param normalizedName 카테고리명(정규화)
     * @return 존재하면 true
     */
    boolean existsByNormalizedName(String normalizedName);

}
