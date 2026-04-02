package by.zeus.demo.dynamicquery.rest;

import by.zeus.demo.base.web.dto.BaseDTO;
import by.zeus.demo.dynamicquery.service.dynamic.DynamicQueryService;
import by.zeus.demo.dynamicquery.service.product.ProductSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@CrossOrigin ( {"http://localhost:4401", "http://localhost:4200"} )

public class ProductSearchController {

    private final ProductSearchService productSearchService;
    private final DynamicQueryService dynamicQueryService;


    public ProductSearchController(ProductSearchService productSearchService, DynamicQueryService dynamicQueryService) {
        this.productSearchService = productSearchService;
        this.dynamicQueryService = dynamicQueryService;
    }

    @GetMapping
    public Map<String, List<? extends BaseDTO>> search(
            @RequestParam String tableName,
            @RequestParam String columnName,
            @RequestParam String value,
            @RequestParam(required = false) Long categoryDetailsId) { // Opsiyonel parametre eklendi
        return productSearchService.searchAndConvert(tableName, columnName, value, categoryDetailsId);
    }
}
