package by.zeus.demo.product.web.rest;

import by.zeus.demo.base.web.dto.BaseDTO;
import by.zeus.demo.base.web.rest.BaseResource;
import by.zeus.demo.category.web.dto.CategoryDTO;
import by.zeus.demo.product.domain.ProductEntity;
import by.zeus.demo.product.facade.ProductFacade;
import by.zeus.demo.product.web.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/products")
@CrossOrigin ( {"http://localhost:4401", "http://localhost:4200"} )
public class ProductResource extends BaseResource<ProductDTO, ProductEntity> {

    @GetMapping("")
    public Page<ProductDTO> getAllProducts(Pageable pageable){
        return getFacade().findAll(pageable);
    }



    @GetMapping("category/{Id}")
    public Page<ProductDTO> findByCategoryId(@PathVariable Long Id, Pageable pageable){
        return getFacade().findByCategoryId(Id,pageable);
    }

    @GetMapping("search/{name}")
    public Page<ProductDTO> findByName(@PathVariable String name, Pageable pageable){
        return getFacade().findByName(name,pageable);
    }

    @GetMapping("productAndDetail/{id}")
    public Map<String, List<? extends BaseDTO>> getProductAndDetails(@PathVariable Long id){
        return getFacade().getProductAndDetails(id);
    }



    public ProductResource(final ProductFacade facade) {
        super(facade);
    }

    @Override
    public Class<?> getLoggerClass() {
        return this.getClass();
    }

    @Override
    public ProductFacade getFacade() {
        return (ProductFacade) super.getFacade();
    }
}
