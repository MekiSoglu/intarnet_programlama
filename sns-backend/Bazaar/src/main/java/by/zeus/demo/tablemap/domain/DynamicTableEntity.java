package by.zeus.demo.tablemap.domain;

import by.zeus.demo.base.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.sql.Date;

@Entity
@Table(name = "dynamic_tables")
public class DynamicTableEntity extends BaseEntity {

    @Column(name = "table_name", nullable = false)
    private String tableName;  // Ana tablo

    @Column(name = "create_date")
    private Date createDate;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }
}
