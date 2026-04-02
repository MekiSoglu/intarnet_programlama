package by.zeus.demo.product.facade;

import by.zeus.demo.base.facade.BaseFacade;
import by.zeus.demo.product.domain.ProductDetailsEntity;
import by.zeus.demo.product.service.ProductDetailsService;
import by.zeus.demo.product.web.dto.ProductDetailsDTO;
import by.zeus.demo.product.web.mapper.ProductDetailsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductDetailsFacade extends BaseFacade<ProductDetailsDTO, ProductDetailsEntity> {
    public ProductDetailsFacade(final ProductDetailsService service,
                                final ProductDetailsMapper mapper) {
        super(service, mapper);
    }

    @Override
    public Class<ProductDetailsDTO> getDtoClass() {
        return ProductDetailsDTO.class;
    }

    @Override
    public ProductDetailsService getService() {
        return (ProductDetailsService) super.getService();
    }

    @Override
    public ProductDetailsMapper getMapper() {
        return (ProductDetailsMapper) super.getMapper();
    }
}
