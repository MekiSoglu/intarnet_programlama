package by.zeus.demo.dynamicquery.service.dynamic;

import by.zeus.demo.tablemap.service.ViewService;
import by.zeus.demo.tablemap.domain.ViewMap;
import by.zeus.demo.tablemap.repository.ViewRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DynamicJoinTable {

    @PersistenceContext
    private EntityManager entityManager;
    private final ViewService viewService;
    private final ViewRepository viewRepository;

    public DynamicJoinTable(ViewService viewService, ViewRepository viewRepository) {
        this.viewService = viewService;
        this.viewRepository = viewRepository;
    }

    /**
     * ✅ 3️⃣ Verilen tablolar için ilişkileri getir
     * @param tableNames List<String> -> İlişkileri alınacak tablolar listesi
     * @return List<Map<String, Object>> -> İlişkili tabloların bilgileri (table_name, related_table, relation_type)
     */

    public List<Map<String, Object>> getTableRelations(List<String> tableNames) {
        if (tableNames == null || tableNames.isEmpty()) {
            throw new IllegalArgumentException("Tablo isimleri boş olamaz.");
        }

        List<Map<String, Object>> relations = new ArrayList<>();
        boolean reverseOrder = false;
        int productIndex = tableNames.indexOf("product");

        // ✅ Eğer `product` varsa, seçim sırasına göre yönü belirle
        if (productIndex != -1) {
            if (productIndex == 0) {
                reverseOrder = true; // Eğer product ilk sırada seçildiyse, tersine çevir
            } else {
                String lastSelectedTable = tableNames.get(productIndex - 1);
                if (!lastSelectedTable.equals("product")) {
                    reverseOrder = true; // Eğer product sonradan seçildiyse, ters çevir
                }
            }
        }

        // 📌 Her tablo için ayrı ayrı sorgu çalıştır
        for (String tableName : tableNames) {
            // 📌 İlk olarak table_name üzerinden sorgu yap
            String query = """
            SELECT table_name, related_table, relation_type
            FROM table_map
            WHERE table_name = :tableName
        """;

            List<Object[]> results = entityManager.createNativeQuery(query)
                    .setParameter("tableName", tableName)
                    .getResultList();

            if (!results.isEmpty()) {
                for (Object[] row : results) {
                    Map<String, Object> relation = new LinkedHashMap<>();

                    if (reverseOrder) {
                        relation.put("table_name", row[1]); // Tersine çevir
                        relation.put("related_table", row[0]);
                    } else {
                        relation.put("table_name", row[0]); // Normal
                        relation.put("related_table", row[1]);
                    }

                    relation.put("relation_type", row[2]);
                    relations.add(relation);
                }
            } else {
                // 📌 Eğer ilk sorgu boşsa, related_table üzerinden tekrar sorgu yap
                String altQuery = """
                SELECT table_name, related_table, relation_type
                FROM table_map
                WHERE related_table = :tableName
            """;

                results = entityManager.createNativeQuery(altQuery)
                        .setParameter("tableName", tableName)
                        .getResultList();

                for (Object[] row : results) {
                    Map<String, Object> relation = new LinkedHashMap<>();

                    if (reverseOrder) {
                        relation.put("table_name", row[1]); // Tersine çevir
                        relation.put("related_table", row[0]);
                    } else {
                        relation.put("table_name", row[1]); // Normal
                        relation.put("related_table", row[0]);
                    }

                    relation.put("relation_type", row[2]);
                    relations.add(relation);
                }
            }
        }

        return relations;
    }




    /**
     * ✅ 1️⃣ Dinamik View oluşturma
     * @param tableNames List<String> -> Birleştirilecek tablolar
     * @param relations List<Map<String, Object>> -> İlişki haritası (table1, table2, relation_type)
     * @param viewName String -> Oluşturulacak View ismi
     * @return String -> Kullanılacak View adı
     */
    @Transactional
    public String createDynamicView(List<String> tableNames, List<Map<String, Object>> relations, String viewName) {
        if (tableNames == null || tableNames.isEmpty() || relations == null || relations.isEmpty()) {
            throw new IllegalArgumentException("Tablo isimleri ve ilişkiler boş olamaz.");
        }

        // 📌 View Adını Belirle
        String relationTableName = String.join("_", tableNames);

        // 📌 Dinamik SELECT oluştur
        StringBuilder queryBuilder = new StringBuilder("CREATE VIEW " + viewName + " AS SELECT ");

        List<String> columns = new ArrayList<>();

        // **📌 Tabloların tüm sütunlarını dinamik olarak getir**
        for (String table : tableNames) {
            List<String> tableColumns = getTableColumns(table); // 🔹 Tüm sütunları getir
            for (String column : tableColumns) {
                columns.add(table + "." + column + " AS " + table + "_" + column);
            }
        }

        queryBuilder.append(String.join(", ", columns));

        // 📌 FROM ve JOIN'leri oluştur
        queryBuilder.append(" FROM ").append(tableNames.get(0));

        Set<String> joinedTables = new HashSet<>();

        for (Map<String, Object> relation : relations) {
            String table1 = relation.get("table_name").toString();
            String table2 = relation.get("related_table").toString();
            String relationType = relation.get("relation_type").toString();

            if (joinedTables.contains(table2)) {
                continue;
            }

            if ("many-to-one".equalsIgnoreCase(relationType) || "one-to-many".equalsIgnoreCase(relationType)) {
                queryBuilder.append(" LEFT JOIN ")
                        .append(table2)
                        .append(" ON ")
                        .append(table1)
                        .append(".id = ")
                        .append(table2)
                        .append(".")
                        .append(table1)
                        .append("_id");

            } else if ("many-to-many".equalsIgnoreCase(relationType)) {
                String joinTable = findJoinTableName(table1, table2);
                if (joinTable != null) {
                    if (!joinedTables.contains(joinTable)) {
                        queryBuilder.append(" LEFT JOIN ")
                                .append(joinTable)
                                .append(" ON ")
                                .append(table1)
                                .append(".id = ")
                                .append(joinTable)
                                .append(".")
                                .append(table1)
                                .append("_id");

                        joinedTables.add(joinTable);
                    }

                    queryBuilder.append(" LEFT JOIN ")
                            .append(table2)
                            .append(" ON ")
                            .append(table2)
                            .append(".id = ")
                            .append(joinTable)
                            .append(".")
                            .append(table2)
                            .append("_id");
                }
            }

            joinedTables.add(table2);
        }

        // 📌 Dinamik View Oluştur
        entityManager.createNativeQuery(queryBuilder.toString()).executeUpdate();

        // 📌 ViewMap tablosuna kaydet
        ViewMap viewMap = new ViewMap();
        viewMap.setRelationTableName(relationTableName);
        viewMap.setViewName(viewName);
        viewService.save(viewMap);

        return viewName;
    }

    private List<String> getTableColumns(String tableName) {
        String query = """
        SELECT column_name 
        FROM information_schema.columns 
        WHERE table_name = :tableName
        ORDER BY ordinal_position
    """;

        return entityManager.createNativeQuery(query)
                .setParameter("tableName", tableName)
                .getResultList();
    }



    /**
     * ✅ 2️⃣ Belirtilen View'den veriyi getir
     * @param viewName String -> Kullanılacak View adı
     * @return List<Map<String, Object>> -> View'den çekilen veriler
     */
    public List<Map<String, Object>> fetchDataFromView(String viewName) {
        // 1️⃣ Tablo kolonlarını çek
        List<String> columnNames = getTableColumns(viewName);

        // 2️⃣ Gereksiz kolonları filtrele (_id, created_by, last_modified_by, created_date)
        List<String> filteredColumns = new ArrayList<>();
        for (String column : columnNames) {
            if (!column.endsWith("_id") &&
                    !column.equals("product_created_by") &&
                    !column.equals("product_last_modified_by") &&
                    !column.equals("created_date") &&
                    !column.equals("product_last_modified_date") &&
                    !column.equals("product_version") &&
                    !column.equals("product_date_create") &&
                    !column.equals("product_created_date")){
                filteredColumns.add(column);
            }
        }

        // 3️⃣ Filtrelenmiş kolonlarla dinamik sorgu hazırla
        String query = "SELECT " + String.join(", ", filteredColumns) + " FROM " + viewName;
        List<Object[]> results = entityManager.createNativeQuery(query).getResultList();

        List<Map<String, Object>> formattedData = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (Object[] row : results) {
            Map<String, Object> rowMap = new LinkedHashMap<>();
            for (int i = 0; i < filteredColumns.size(); i++) {
                Object value = row[i];
                if (value instanceof Date) {
                    value = dateFormat.format(value); // Tarih formatını dönüştür
                }
                rowMap.put(filteredColumns.get(i), value);
            }
            formattedData.add(rowMap);
        }

        return formattedData;
    }

    /**
     * ✅ Belirtilen View'den filtrelenmiş veriyi getir
     * @param viewName String -> Kullanılacak View adı
     * @param columnName String -> Filtreleme yapılacak kolon adı
     * @param columnValue Object -> Aranacak değer
     * @return List<Map<String, Object>> -> Filtrelenmiş veri listesi
     */
    public List<Map<String, Object>> fetchFilteredDataFromView(String viewName, String columnName, Object columnValue) {
        // 1️⃣ Önce tüm verileri getir
        List<Map<String, Object>> allData = fetchDataFromView(viewName);

        // 2️⃣ Gelen verileri filtrele (Kolon adı ve değerine göre)
        List<Map<String, Object>> filteredData = new ArrayList<>();

        for (Map<String, Object> row : allData) {
            if (row.containsKey(columnName)) {
                Object dbValue = row.get(columnName); // Veritabanındaki gerçek değer

                // ✅ Eğer değer `Integer` ise ve `columnValue` Integer olarak parse edilebiliyorsa
                if (dbValue instanceof Integer && columnValue instanceof String) {
                    try {
                        int parsedValue = Integer.parseInt((String) columnValue);
                        if (dbValue.equals(parsedValue)) {
                            filteredData.add(row);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Sayısal olmayan değer: " + columnValue);
                    }
                }
                // ✅ Eğer değer `Double` veya `BigDecimal` ise
                else if ((dbValue instanceof Double || dbValue instanceof BigDecimal) && columnValue instanceof String) {
                    try {
                        double parsedValue = Double.parseDouble((String) columnValue);
                        if (dbValue.equals(parsedValue)) {
                            filteredData.add(row);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Geçersiz sayı formatı: " + columnValue);
                    }
                }
                // ✅ Eğer değer `Boolean` ise (Örneğin `true` veya `false` karşılaştırması)
                else if (dbValue instanceof Boolean && columnValue instanceof String) {
                    if (Boolean.parseBoolean((String) columnValue) == (Boolean) dbValue) {
                        filteredData.add(row);
                    }
                }
                // ✅ Eğer değer `Date` ise ve gelen değer `dd/MM/yyyy` formatında parse edilebiliyorsa
                else if (dbValue instanceof Date && columnValue instanceof String) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date parsedDate = dateFormat.parse((String) columnValue);
                        if (((Date) dbValue).equals(parsedDate)) {
                            filteredData.add(row);
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ Geçersiz tarih formatı: " + columnValue);
                    }
                }
                // ✅ Eğer değer `String` ise (Normal String karşılaştırması)
                else if (dbValue instanceof String && columnValue instanceof String) {
                    if (dbValue.equals(columnValue)) {
                        filteredData.add(row);
                    }
                }
            }
        }

        return filteredData;
    }





    /** ✅ Many-to-Many için ara tabloyu bul */
    private String findJoinTableName(String table1, String table2) {
        String joinTable1 = table1 + "_" + table2;
        String joinTable2 = table2 + "_" + table1;

        String checkTableQuery = """
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_name IN (:joinTable1, :joinTable2) 
            LIMIT 1
        """;

        List<String> result = entityManager.createNativeQuery(checkTableQuery)
                .setParameter("joinTable1", joinTable1)
                .setParameter("joinTable2", joinTable2)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> deleteCari(String viewName) {
        try {
            // Öncelikle ViewMap tablosundan kaydı sil
            viewService.delete(viewName);

            // DROP VIEW IF EXISTS sorgusu için string birleştirme yap
            String query = "DROP VIEW IF EXISTS " + viewName;
            entityManager.createNativeQuery(query).executeUpdate();

            // ViewMap tablosundan kaydı sil
            String query2 = "DELETE FROM view_map WHERE view_name = :viewName";
            entityManager.createNativeQuery(query2)
                         .setParameter("viewName", viewName)
                         .executeUpdate();

            // ✅ JSON formatında response döndür
            return ResponseEntity.ok(Map.of("message", "Cari başarıyla silindi: " + viewName));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Silme işlemi sırasında hata oluştu: " + e.getMessage()));
        }
    }

}
