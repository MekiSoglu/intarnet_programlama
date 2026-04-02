package by.zeus.demo.product.web.dto;

import by.zeus.demo.base.web.dto.BaseDTO;

import java.math.BigDecimal;
import java.util.Date;

public class ProductWithDetailsDTO extends BaseDTO {
    Long id;

    Long categoryId;

    String sku;

    String name;

    boolean active;

    BigDecimal unitPrice;


    int unitStocks;

    Date dataCreate;

    Date lastUpdate;

    String imageUrl;

    String description;
}
