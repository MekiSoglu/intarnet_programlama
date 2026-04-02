package by.zeus.demo.category.repository;

import by.zeus.demo.base.repository.BaseRepository;
import by.zeus.demo.category.domain.CategoryEntity;
import by.zeus.demo.category.domain.CategoryDetailsEntity;
import by.zeus.demo.category.web.dto.MinCategoryDTO;
import by.zeus.demo.product.domain.ProductEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin ( "http://localhost:4200" )
public interface CategoryRepository extends BaseRepository<CategoryEntity> {

    @Query("SELECT c FROM CategoryEntity c WHERE c.id IN (:Ids)")
    List<CategoryEntity> findCategoriesBy(List<Long> Ids);

    @Query("SELECT c FROM CategoryEntity c JOIN c.productEntitySet p WHERE p IN :productEntityList")
    CategoryEntity findCategoryByProductEntitySet(@Param("productEntityList") List<ProductEntity> productEntityList);

    @Query("SELECT c.categoryDetailsEntityList FROM CategoryEntity c WHERE c.id = :id")
    List<CategoryDetailsEntity> findCategoryDetailsByCategoryId(Long id);

    @Query("SELECT new by.zeus.demo.category.web.dto.MinCategoryDTO(c.categoryName, c.id, c.parentId) FROM CategoryEntity c")
    List<MinCategoryDTO> findCategoryEntityBy();
}
