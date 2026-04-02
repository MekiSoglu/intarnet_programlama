package by.zeus.demo.category.web.dto;

import by.zeus.demo.base.web.dto.BaseDTO;

import java.util.ArrayList;
import java.util.List;

public class CategoryDTO extends BaseDTO {

    Long parentId;

    String categoryName;

   // Set<ProductDto> productSet =new HashSet<>();

    List<CategoryDetailsDTO> categoryDetailsList=new ArrayList<>();


    int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(final Long parentId) {
        this.parentId = parentId;
    }

    /*

    public Set<ProductDto> getProductSet() {
        return productSet;
    }

    public void setProductSet(final Set<ProductDto> productSet) {
        this.productSet = productSet;
    }


     */
    public List<CategoryDetailsDTO> getCategoryDetailsList() {
        return categoryDetailsList;
    }

    public void setCategoryDetailsList(final List<CategoryDetailsDTO> categoryDetailsList) {
        this.categoryDetailsList = categoryDetailsList;
    }
}
