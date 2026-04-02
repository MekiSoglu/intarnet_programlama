package by.zeus.demo.category.service;

import by.zeus.demo.base.repository.BaseRepository;
import by.zeus.demo.base.service.BaseService;
import by.zeus.demo.category.domain.CategoryDetailsEntity;
import by.zeus.demo.category.domain.CategoryEntity;
import by.zeus.demo.category.repository.CategoryRepository;
import by.zeus.demo.category.web.dto.CategoryDetailsDTO;
import by.zeus.demo.category.web.dto.MinCategoryDTO;
import by.zeus.demo.category.web.mapper.CategoryDetailsMapper;
import by.zeus.demo.category.web.mapper.CategoryMapper;
import by.zeus.demo.product.domain.ProductEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService extends BaseService<CategoryEntity> {

    private  final CategoryDetailsService categoryDetailsService;
    private final  CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    private final CategoryDetailsMapper categoryDetailsMapper;


    public CategoryService(BaseRepository<CategoryEntity> repository, CategoryDetailsService categoryDetailsService,
                           CategoryRepository categoryRepository, final CategoryMapper categoryMapper,
                           final CategoryDetailsMapper categoryDetailsMapper) {
        super(repository);
        this.categoryDetailsService = categoryDetailsService;
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.categoryDetailsMapper = categoryDetailsMapper;
    }

    public CategoryEntity findByCategoryProductId(ProductEntity productEntity){
        List<ProductEntity> productEntities =new ArrayList<>();
        productEntities.add(productEntity);
        CategoryEntity categoryEntity =categoryRepository.findCategoryByProductEntitySet(productEntities);
        return categoryEntity;
    }

    public List<CategoryDetailsDTO> getCategoryDetails(Long categoryId) {
        List<CategoryDetailsEntity> categoryDetailsEntityList = categoryRepository.findCategoryDetailsByCategoryId(categoryId);
        List<CategoryDetailsDTO> categoryDetailsDTOS =new ArrayList<>();
        for(CategoryDetailsEntity categoryDetailsEntity : categoryDetailsEntityList){
            categoryDetailsDTOS.add(categoryDetailsMapper.toDto(categoryDetailsEntity));
        }
       return categoryDetailsDTOS;
    }
    public List<CategoryEntity> findAll(List<Long> Ids){
        return categoryRepository.findCategoriesBy(Ids);
    }


    public List<MinCategoryDTO> getMinCategoryDTO() {
        return categoryRepository.findCategoryEntityBy();
    }
}
