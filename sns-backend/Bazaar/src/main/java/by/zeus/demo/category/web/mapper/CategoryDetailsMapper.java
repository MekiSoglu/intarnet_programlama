package by.zeus.demo.category.web.mapper;

import by.zeus.demo.base.web.mapper.BaseMapper;
import by.zeus.demo.category.domain.CategoryDetailsEntity;
import by.zeus.demo.category.web.dto.CategoryDetailsDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface CategoryDetailsMapper extends BaseMapper<CategoryDetailsDTO, CategoryDetailsEntity> {

}
