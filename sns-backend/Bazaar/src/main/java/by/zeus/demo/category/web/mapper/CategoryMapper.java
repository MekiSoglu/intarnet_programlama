package by.zeus.demo.category.web.mapper;

import by.zeus.demo.base.web.mapper.BaseMapper;
import by.zeus.demo.category.domain.CategoryEntity;
import by.zeus.demo.category.web.dto.CategoryDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CategoryMapper extends BaseMapper<CategoryDTO, CategoryEntity> {

    //@Mapping(target = "productSet", conditionExpression = "java(entity.getProductSet())")
   // public abstract CategoryDto toDto(Category entity);



}
