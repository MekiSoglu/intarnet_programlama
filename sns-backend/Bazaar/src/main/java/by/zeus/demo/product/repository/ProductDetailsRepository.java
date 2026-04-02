package by.zeus.demo.product.repository;

import by.zeus.demo.base.repository.BaseRepository;
import by.zeus.demo.product.domain.ProductDetailsEntity;

import java.util.List;

public interface ProductDetailsRepository extends BaseRepository<ProductDetailsEntity> {
    List<ProductDetailsEntity> findByProductId(Long productId);
}
