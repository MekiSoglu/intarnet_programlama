package by.zeus.demo.category.facade;

import by.zeus.demo.base.facade.BaseFacade;
import by.zeus.demo.category.domain.CategoryEntity;
import by.zeus.demo.category.service.CategoryService;
import by.zeus.demo.category.web.dto.CategoryDTO;
import by.zeus.demo.category.web.mapper.CategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryFacade extends BaseFacade<CategoryDTO, CategoryEntity> {
    public CategoryFacade(final CategoryService service,
                          final CategoryMapper mapper) {
        super(service, mapper);
    }

    @Override
    public Class<CategoryDTO> getDtoClass() {
        return CategoryDTO.class;
    }
}
