package by.zeus.demo.product.web.dto;

import by.zeus.demo.base.web.dto.BaseDTO;

public class ProductDetailsDTO extends BaseDTO {
    private String value;
    private Long productId;
    private Long categoryDetailsId;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(final Long productId) {
        this.productId = productId;
    }

    public Long getCategoryDetailsId() {
        return categoryDetailsId;
    }

    public void setCategoryDetailsId(final Long categoryDetailsId) {
        this.categoryDetailsId = categoryDetailsId;
    }
}
