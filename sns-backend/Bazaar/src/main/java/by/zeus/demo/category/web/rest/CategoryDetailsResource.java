package by.zeus.demo.category.web.rest;

import by.zeus.demo.base.web.rest.BaseResource;
import by.zeus.demo.category.domain.CategoryDetailsEntity;
import by.zeus.demo.category.facade.CategoryDetailsFacade;
import by.zeus.demo.category.web.dto.CategoryDetailsDTO;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/catDetails")
@CrossOrigin ( {"http://localhost:4401", "http://localhost:4200" , "http://localhost:80"} )

public class CategoryDetailsResource extends BaseResource<CategoryDetailsDTO, CategoryDetailsEntity> {


    public CategoryDetailsResource(final CategoryDetailsFacade facade) {
        super(facade);
    }

    @Override
    public Class<?> getLoggerClass() {
        return this.getClass();
    }
}
