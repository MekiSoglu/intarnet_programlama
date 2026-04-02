package by.zeus.demo.tablemap.domain;

import by.zeus.demo.tablemap.service.JsonConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.databind.JsonNode;

@Entity
@Table(name = "islem_table_map")
public class IslemTableMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "islem_adi")
    private String islemAdi;

    @Convert(converter = JsonConverter.class) // JSON dönüşümü için Converter kullanımı
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "islem_json", columnDefinition = "jsonb")
    private JsonNode islemJson;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIslemAdi() { return islemAdi; }
    public void setIslemAdi(String islemAdi) { this.islemAdi = islemAdi; }

    public JsonNode getIslemJson() { return islemJson; }
    public void setIslemJson(JsonNode islemJson) { this.islemJson = islemJson; }
}

