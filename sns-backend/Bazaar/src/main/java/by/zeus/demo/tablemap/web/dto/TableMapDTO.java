package by.zeus.demo.tablemap.web.dto;

import by.zeus.demo.base.web.dto.BaseDTO;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class TableMapDTO extends BaseDTO {


    private Long id;

    private String tableName;  // Ana tablo

    private String relatedTable;  // Bağlı tablo

    private String relationType;  // one-to-many, many-to-many gibi

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    public String getRelatedTable() {
        return relatedTable;
    }

    public void setRelatedTable(final String relatedTable) {
        this.relatedTable = relatedTable;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(final String relationType) {
        this.relationType = relationType;
    }
}
