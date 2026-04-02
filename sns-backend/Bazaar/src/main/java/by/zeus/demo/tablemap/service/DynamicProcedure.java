package by.zeus.demo.tablemap.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DynamicProcedure {

    @PersistenceContext
    private EntityManager entityManager;

    /** ✅ Dinamik PostgreSQL Prosedürü Oluştur */
    @Transactional
    public void createProcedure(Map<String, Object> jsonData) {
        String procedureName = ((String) jsonData.get("name")).replace(" ", "_").toLowerCase();
        Map<String, Object> procedureData = (Map<String, Object>) jsonData.get("jsonData");
        List<Map<String, Object>> columns = (List<Map<String, Object>>) procedureData.get("columns");
        List<Map<String, Object>> inputs = (List<Map<String, Object>>) procedureData.get("inputs");

        // ✅ 1️⃣ Parametreleri oluştur (input değerleri + her tablo için ayrı affected_id)
        StringBuilder paramList = new StringBuilder();
        for (Map<String, Object> input : inputs) {
            paramList.append(input.get("name")).append(" ").append(input.get("dataType")).append(",");
        }

        // ✅ Her tablo için ayrı ID parametresi ekle
        Set<String> affectedTables = new HashSet<>();
        for (Map<String, Object> column : columns) {
            affectedTables.add((String) column.get("table"));
        }

        Map<String, String> tableIdMap = new HashMap<>();
        for (String table : affectedTables) {
            String tableIdParam = table + "_id";
            paramList.append(tableIdParam).append(" INTEGER,");
            tableIdMap.put(table, tableIdParam);
        }

        // ✅ Son virgülü kaldır
        if (paramList.length() > 0) {
            paramList.setLength(paramList.length() - 1);
        }

        // ✅ 2️⃣ SQL UPDATE işlemlerini oluştur
        StringBuilder sqlActions = new StringBuilder();
        for (Map<String, Object> column : columns) {
            String tableName = (String) column.get("table");
            String columnName = (String) column.get("column");
            String operation = (String) column.get("operation");

            String tableIdParam = tableIdMap.get(tableName);

            if ("increase".equalsIgnoreCase(operation)) {
                sqlActions.append("UPDATE ").append(tableName)
                          .append(" SET ").append(columnName).append(" = ").append(columnName)
                          .append(" + ").append(inputs.get(0).get("name"))
                          .append(" WHERE id = ").append(tableIdParam).append("; ");
            } else if ("decrease".equalsIgnoreCase(operation)) {
                sqlActions.append("UPDATE ").append(tableName)
                          .append(" SET ").append(columnName).append(" = ").append(columnName)
                          .append(" - ").append(inputs.get(0).get("name"))
                          .append(" WHERE id = ").append(tableIdParam).append("; ");
            }
        }

        // ✅ 3️⃣ Dinamik `CREATE PROCEDURE` sorgusunu oluştur
        String sql = "CREATE OR REPLACE PROCEDURE " + procedureName + "(" + paramList + ") LANGUAGE plpgsql AS $$ " +
                "BEGIN " + sqlActions + " END $$;";

        System.out.println("Generated SQL: " + sql);
        entityManager.createNativeQuery(sql).executeUpdate();
    }



    /** ✅ Prosedürü Çalıştır */
    @Transactional
    public void executeProcedure(String procedureName, Map<String, Object> params) {
        List<Map<String, Object>> selectedIds = (List<Map<String, Object>>) params.get("selectedIds");
        List<Map<String, Object>> inputValues = (List<Map<String, Object>>) params.get("inputValues");

        // ✅ Parametreleri birleştir
        StringBuilder paramPlaceholders = new StringBuilder();
        List<Object> paramValues = new ArrayList<>();

        // ✅ Input Parametrelerini Ekle
        for (Map<String, Object> input : inputValues) {
            paramPlaceholders.append("CAST(? AS INTEGER),");
            paramValues.add(Integer.parseInt(input.get("value").toString())); // **String → Integer dönüşümü**
        }

        // ✅ Seçili Satırların ID'lerini Ekle
        for (Map<String, Object> row : selectedIds) {
            paramPlaceholders.append("CAST(? AS INTEGER),");
            paramValues.add(Integer.parseInt(row.get("id").toString())); // **String → Integer dönüşümü**
        }

        // ✅ Son virgülü kaldır
        if (paramPlaceholders.length() > 0) {
            paramPlaceholders.setLength(paramPlaceholders.length() - 1);
        }

        // ✅ PostgreSQL Prosedürünü Çağır
        String callSql = "CALL " + procedureName + "(" + paramPlaceholders + ")";
        Query query = entityManager.createNativeQuery(callSql);

        // ✅ Parametreleri Doğru Tipte Ekle
        for (int i = 0; i < paramValues.size(); i++) {
            query.setParameter(i + 1, paramValues.get(i));
        }

        query.executeUpdate();
    }


}
