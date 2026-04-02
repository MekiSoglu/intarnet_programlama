package by.zeus.demo.tablemap.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "view_map") // ✅ Tablo adı view_map olarak ayarlandı
public class ViewMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "relation_table_name")
    private String relationTableName;

    @Column(name = "view_name")
    private String viewName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRelationTableName() {
        return relationTableName;
    }

    public void setRelationTableName(String relationTableName) {
        this.relationTableName = relationTableName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
