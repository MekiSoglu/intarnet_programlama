package by.zeus.demo.category.web.rest;

import by.zeus.demo.base.web.rest.BaseResource;
import by.zeus.demo.category.domain.CategoryEntity;
import by.zeus.demo.category.facade.CategoryFacade;
import by.zeus.demo.category.service.CategoryService;
import by.zeus.demo.category.web.dto.CategoryDTO;
import by.zeus.demo.category.web.dto.CategoryDetailsDTO;
import by.zeus.demo.category.web.dto.MinCategoryDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin( {"http://localhost:4401", "http://localhost:4200"} )

public class CategoryResource extends BaseResource<CategoryDTO, CategoryEntity> {

    private final CategoryService categoryService;

    public CategoryResource(final CategoryFacade facade, final CategoryService categoryService) {
        super(facade);
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDTO> getAllCategories() {
     log.debug("REST request to get all categories");
        return getFacade().findAll();
    }

    @GetMapping("/details/{id}")
    public List<CategoryDetailsDTO> getDetails(@PathVariable("id") Long id) {
        return categoryService.getCategoryDetails(id);
    }

    @GetMapping("/minCategory")
    public List<MinCategoryDTO> getMinCategoryDTO(){
        return categoryService.getMinCategoryDTO();
    }

    @Override
    public Class<?> getLoggerClass() {
        return this.getClass();
    }
    @Override
    public CategoryFacade getFacade() {
        return (CategoryFacade) super.getFacade();
    }
}
