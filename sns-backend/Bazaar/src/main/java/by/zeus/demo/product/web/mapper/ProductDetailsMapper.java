package by.zeus.demo.product.web.mapper;

import by.zeus.demo.base.web.mapper.BaseMapper;
import by.zeus.demo.product.domain.ProductDetailsEntity;
import by.zeus.demo.product.web.dto.ProductDetailsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductDetailsMapper extends BaseMapper<ProductDetailsDTO, ProductDetailsEntity> {
}
