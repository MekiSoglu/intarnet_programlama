package by.zeus.demo.product.web.mapper;

import by.zeus.demo.base.web.mapper.BaseMapper;
import by.zeus.demo.product.domain.ProductEntity;
import by.zeus.demo.product.web.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper extends BaseMapper<ProductDTO, ProductEntity> {


}
