package by.zeus.demo.product.web.rest;

import by.zeus.demo.base.web.rest.BaseResource;
import by.zeus.demo.product.domain.ProductDetailsEntity;
import by.zeus.demo.product.facade.ProductDetailsFacade;
import by.zeus.demo.product.web.dto.ProductDetailsDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prdDetails")
@CrossOrigin ( {"http://localhost:4401", "http://localhost:4200"} )

public class ProductDetailsResource extends BaseResource<ProductDetailsDTO, ProductDetailsEntity> {


    public ProductDetailsResource(final ProductDetailsFacade facade) {
        super(facade);
    }

    @Override
    public Class<?> getLoggerClass() {
        return this.getClass();
    }
    @Override
    public ProductDetailsFacade getFacade() {
        return (ProductDetailsFacade) super.getFacade();
    }
}
