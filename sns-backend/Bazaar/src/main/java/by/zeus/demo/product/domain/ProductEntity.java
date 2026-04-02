package by.zeus.demo.product.domain;

import by.zeus.demo.base.domain.BaseEntity;
import by.zeus.demo.category.domain.CategoryEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "product")
public class ProductEntity extends BaseEntity {


    public CategoryEntity getCategory() {
        return categoryEntity;
    }

    public void setCategory(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }

    @ManyToOne
    @JoinColumn(name = "category_id",nullable = false ,insertable = false,updatable = false)
    CategoryEntity categoryEntity;

    @Column(name = "category_id")
    private Long categoryId;

    @Column
    String sku;

    @Column
    String name;

    @Column
    BigDecimal unitPrice;

    @Column
    boolean active;

    @Column
    int unitsInStock;

    @Column
    @CreationTimestamp
    Date dateCreate;

    @Column
    @CreationTimestamp
    Date lastUpdate;


    @Column
    String imageUrl;


    @Column
    String description;



    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //add jenkis test final2


    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getUnitsInStock() {
        return unitsInStock;
    }

    public void setUnitsInStock(int unitsInStock) {
        this.unitsInStock = unitsInStock;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryEntity getCategoryEntity() {
        return categoryEntity;
    }

    public void setCategoryEntity(final CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final Long categoryId) {
        this.categoryId = categoryId;
    }



}
