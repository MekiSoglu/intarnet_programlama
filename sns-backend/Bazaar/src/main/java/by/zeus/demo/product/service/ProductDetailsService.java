package by.zeus.demo.product.service;

import by.zeus.demo.base.repository.BaseRepository;
import by.zeus.demo.product.domain.ProductDetailsEntity;
import by.zeus.demo.product.repository.ProductDetailsRepository;
import by.zeus.demo.base.service.BaseService;
import by.zeus.demo.product.web.dto.ProductDetailsDTO;
import by.zeus.demo.product.web.mapper.ProductDetailsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductDetailsService extends BaseService<ProductDetailsEntity> {
    private final ProductDetailsRepository productDetailsRepository;
    private final ProductDetailsMapper productDetailsMapper;

    public ProductDetailsService(BaseRepository<ProductDetailsEntity> repository, ProductDetailsRepository productDetailsRepository,
                                 final ProductDetailsMapper productDetailsMapper) {
        super(repository);
        this.productDetailsRepository = productDetailsRepository;
        this.productDetailsMapper = productDetailsMapper;
    }

    public List<ProductDetailsEntity> getProductDetails(Long productId){
        return productDetailsRepository.findByProductId(productId);
    }

    public ProductDetailsDTO getProductDetailsDto (ProductDetailsEntity productDetailsEntity){
         ProductDetailsDTO productDetailsDTO = productDetailsMapper.toDto(productDetailsEntity);
         return productDetailsDTO;
    }

}
