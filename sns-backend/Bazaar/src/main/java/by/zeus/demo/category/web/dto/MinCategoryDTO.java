package by.zeus.demo.category.web.dto;

import by.zeus.demo.base.web.dto.BaseDTO;

public class MinCategoryDTO extends BaseDTO {

    private String categoryName;
    private Long id;
    private Long parentId;

    public MinCategoryDTO(String categoryName, Long id, Long parentId) {
        this.categoryName = categoryName;
        this.id = id;
        this.parentId = parentId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(final Long parentId) {
        this.parentId = parentId;
    }
}
