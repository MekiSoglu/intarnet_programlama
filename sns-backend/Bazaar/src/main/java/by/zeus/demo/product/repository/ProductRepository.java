package by.zeus.demo.product.repository;

import by.zeus.demo.base.repository.BaseRepository;
import by.zeus.demo.product.domain.ProductEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@CrossOrigin ( "http://localhost:4200" )
public interface ProductRepository extends BaseRepository<ProductEntity> {
    List<ProductEntity> findByCategoryId(@Param("id") Long id);

    @Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
    Optional<ProductEntity> findById(@Param("id") Long id);
    List<ProductEntity> findByNameContaining(@Param("name") String name);

    @Query("SELECT p , pd FROM ProductEntity p inner join ProductDetailsEntity pd on pd.productId = p.id Where p.categoryId = :id")
    List<Object[]> findProductsWithDetailsByCategoryId(@Param("id") Long id);

    @Query("select c.id from CategoryEntity c where c.parentId = :id")
    List<Long> findBy(@Param("id") Long id);

    @Query("SELECT p , pd FROM ProductEntity p inner join ProductDetailsEntity pd on pd.productId = p.id")
    List<Object[]> findProductsWithDetailsBy();

    @Query("SELECT p, pd FROM ProductEntity p INNER JOIN ProductDetailsEntity pd ON pd.productId = p.id WHERE p.id IN :productIds")
    List<Object[]> findProductsWithDetailsByIds(@Param("productIds") List<Long> productIds);

    @Query("SELECT cd.id, cd.name FROM CategoryDetailsEntity cd WHERE cd.id IN :categoryDetailIds")
    List<Object[]> findDetailsNames(@Param("categoryDetailIds") List<Long> categoryDetailIds);



}
