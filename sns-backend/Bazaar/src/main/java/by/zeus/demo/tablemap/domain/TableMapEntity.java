package by.zeus.demo.tablemap.domain;


import by.zeus.demo.base.domain.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "table_map")

public class TableMapEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name", nullable = false)
    private String tableName;  // Ana tablo

    @Column(name = "related_table", nullable = false)
    private String relatedTable;  // Bağlı tablo

    @Column(name = "relation_type", nullable = false)
    private String relationType;  // one-to-many, many-to-many gibi

    @Column(name = "relation_colum_name", nullable = false)
    private String relationColumName;

    public String getRelationColumName() {
        return relationColumName;
    }

    public void setRelationColumName(final String relationColumName) {
        this.relationColumName = relationColumName;
    }

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

