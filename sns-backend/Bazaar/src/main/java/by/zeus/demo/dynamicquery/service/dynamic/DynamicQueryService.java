package by.zeus.demo.dynamicquery.service.dynamic;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DynamicQueryService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DynamicQueryService(JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public List<Map<String, Object>> executeDynamicQuery(String tableName, String columnName, String value) {
        if (!isValidIdentifier(tableName) || !isValidIdentifier(columnName)) {
            throw new IllegalArgumentException("Geçersiz tablo veya sütun adı!");
        }

        Map<String, Object> params = new HashMap<>();

        // **Many-to-Many FK sorgulanıyorsa, Ara Tablo Kullan!**
        if (columnName.endsWith("_id")) {
            String relatedTable = columnName.replace("_id", ""); // ✅ İlgili tabloyu al
            String joinTable = findJoinTableName(tableName, relatedTable); // ✅ Ara Tabloyu Bul

            if (joinTable != null) {
                // ✅ Tabloların ilişkisini doğru yönlendirmek için JOIN yönünü belirle
                String primaryKeyColumn = tableName + "_id";
                String foreignKeyColumn = relatedTable + "_id";

                if (!joinTable.contains(tableName)) {
                    // Eğer ara tablo "relatedTable_tableName" şeklinde oluşturulmuşsa, yönü ters çevir
                    primaryKeyColumn = relatedTable + "_id";
                    foreignKeyColumn = tableName + "_id";
                }

                // ✅ Many-to-Many ilişkisinde doğru tabloyu getir
                String sql = """
                SELECT main_table.*
                FROM %s main_table
                WHERE main_table.id IN (
                    SELECT %s
                    FROM %s
                    WHERE %s = :value
                )
            """.formatted(tableName, foreignKeyColumn, joinTable, primaryKeyColumn);

                params.put("value", convertValueType(value)); // 🔄 Değer Türünü Algıla
                return namedParameterJdbcTemplate.queryForList(sql, params);
            }
        }

        // **Normal FK veya Normal Kolon Sorgusu**
        String sql = "SELECT * FROM " + tableName + " WHERE " + columnName + " = :value";
        params.put("value", convertValueType(value));
        return namedParameterJdbcTemplate.queryForList(sql, params);
    }


    private String findJoinTableName(String table1, String table2) {
        String joinTable1 = table1 + "_" + table2;
        String joinTable2 = table2 + "_" + table1;

        String checkTableQuery = "SELECT table_name FROM information_schema.tables WHERE table_name IN (:joinTable1, :joinTable2) LIMIT 1";
        List<String> result = namedParameterJdbcTemplate.queryForList(checkTableQuery, Map.of("joinTable1", joinTable1, "joinTable2", joinTable2), String.class);

        return result.isEmpty() ? null : result.get(0); // ✅ Ara Tabloyu Döndür
    }

    private Object convertValueType(String value) {
        if (isInteger(value)) {
            return Integer.parseInt(value); // 🔄 INTEGER
        } else if (isValidDate(value)) {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd")); // 🔄 DATE
        }
        return value; // 🔄 STRING
    }




    // **SQL Injection'ı önlemek için sütun ve tablo isimlerini doğrula**
    private boolean isValidIdentifier(String identifier) {
        return identifier != null && identifier.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }

    // **Gelen değerin INTEGER olup olmadığını kontrol et**
    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // **Gelen değerin DATE olup olmadığını kontrol et**
    private boolean isValidDate(String value) {
        try {
            LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }



}

