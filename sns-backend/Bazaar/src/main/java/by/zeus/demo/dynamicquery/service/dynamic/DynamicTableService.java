package by.zeus.demo.dynamicquery.service.dynamic;

import by.zeus.demo.tablemap.service.TableMapService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DynamicTableService {

    private final TableMapService tableMapService;

    @PersistenceContext
    private EntityManager entityManager;

    public DynamicTableService(TableMapService tableMapService) {
        this.tableMapService = tableMapService;
    }

    /** ✅ Many-to-Many İlişkisini Ekleme */
    /** ✅ Many-to-Many İlişkisini Ara Tabloya Ekleme */
    /** ✅ Many-to-Many İlişkisini Ara Tabloya Ekleme */
    @Transactional
    public void insertManyToManyRelation(String joinTable, String table1, String table2, Long recordId, List<Long> relatedIds) {
        for (Long relatedId : relatedIds) {
            String insertQuery = "INSERT INTO " + joinTable + " (" + table1 + "_id, " + table2 + "_id) VALUES (:recordId, :relatedId)";

            entityManager.createNativeQuery(insertQuery)
                    .setParameter("recordId", recordId)  // ✅ Ana tablonun ID'si
                    .setParameter("relatedId", relatedId)  // ✅ Bağlı tablonun ID'si
                    .executeUpdate();
        }
    }


    /** ✅ Many-to-Many İlişkisini Kontrol Et */
    private boolean isManyToManyTable(String tableName) {
        return tableName.contains("_") && findJoinTableName(tableName.split("_")[0], tableName.split("_")[1]) != null;
    }

    /** ✅ Ara Tablo İsmini Otomatik Bul */
    private String findJoinTableName(String table1, String table2) {
        String joinTable1 = table1 + "_" + table2;
        String joinTable2 = table2 + "_" + table1;

        String checkTableQuery = "SELECT table_name FROM information_schema.tables WHERE table_name IN (:joinTable1, :joinTable2) LIMIT 1";
        List<String> result = entityManager.createNativeQuery(checkTableQuery)
                .setParameter("joinTable1", joinTable1)
                .setParameter("joinTable2", joinTable2)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    /** ✅ Yeni veri ekleme */
    @Transactional
    public Long insertIntoTable(String tableName, Map<String, Object> values) {
        // Many-to-Many ilişkisinde verileri bu metod ile eklememeliyiz


        StringJoiner columns = new StringJoiner(", ");
        StringJoiner params = new StringJoiner(", ");

        // 🛠 Many-to-Many FK'ları ayrı listede saklayacağız
        Map<String, List<Long>> manyToManyValues = new HashMap<>();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String columnName = entry.getKey();

            if (isManyToManyForeignKey(tableName, columnName)) {
                // 🛠 Eğer Many-to-Many FK ise, ayrı bir map'e kaydediyoruz
                List<Long> relatedIds = manyToManyValues.getOrDefault(columnName, new ArrayList<>());

                // 🎯 `product_id` değeri Map olabilir, bunu kontrol edelim
                Object productValue = values.get(columnName);
                if (productValue instanceof Map) {
                    // 🛠 Eğer `product_id` bir Map ise, içindeki `id` değerini al
                    Map<String, Object> productMap = (Map<String, Object>) productValue;
                    if (productMap.containsKey("id")) {
                        relatedIds.add(Long.parseLong(productMap.get("id").toString())); // ✅ ID'yi al ve Long'a çevir
                    }
                } else if (productValue instanceof String || productValue instanceof Integer) {
                    // 🛠 Eğer doğrudan bir String ya da Integer ID geliyorsa, Long'a çevir
                    relatedIds.add(Long.parseLong(productValue.toString()));
                }

                // ✅ Güncellenmiş Many-to-Many ID listesini kaydet
                manyToManyValues.put(columnName, relatedIds);
            } else {
                // 🛠 Many-to-One veya normal kolonlar tabloya eklenir
                columns.add(columnName);
                params.add("'" + entry.getValue().toString() + "'");
            }
        }


        String query = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + params + ") RETURNING id";
        Object insertedId = entityManager.createNativeQuery(query).getSingleResult();
        Long recordId = insertedId != null ? ((Number) insertedId).longValue() : null;

        if (recordId != null && !manyToManyValues.isEmpty()) {
            for (Map.Entry<String, List<Long>> entry : manyToManyValues.entrySet()) {
                String relatedTable = entry.getKey().replace("_id", ""); // `_id` kısmını kaldırarak asıl tabloyu bul
                String joinTable = findJoinTableName(tableName, relatedTable);
                insertManyToManyRelation(joinTable, tableName, relatedTable, recordId, entry.getValue());
            }
        }

        return recordId;
    }


    /** ✅ Many-to-Many Foreign Key olup olmadığını kontrol eden yardımcı metod */
    private boolean isManyToManyForeignKey(String tableName, String columnName) {
        String relatedTable = columnName.replace("_id", "");  // ✅ `_id` kısmını kaldırarak asıl tabloyu bul
        String query = """
        SELECT COUNT(*) FROM table_map 
        WHERE table_name = :tableName AND related_table = :relatedTable AND relation_type = 'many-to-many'
    """;

        Long count = ((Number) entityManager.createNativeQuery(query)
                .setParameter("tableName", tableName)
                .setParameter("relatedTable", relatedTable)
                .getSingleResult()).longValue();

        return count > 0;
    }

    /** ✅ Many-to-One olup olmadığını kontrol eden yardımcı metod */
    private boolean isManyToOneTable(String tableName) {
        String query = """
        SELECT COUNT(*) FROM table_map 
        WHERE table_name = :tableName AND relation_type = 'many-to-one'
    """;

        Long count = ((Number) entityManager.createNativeQuery(query)
                .setParameter("tableName", tableName)
                .getSingleResult()).longValue();

        return count > 0;
    }





    /** ✅ Var olan veriyi güncelleme */
    @Transactional
    public void updateTable(String tableName, Long id, Map<String, Object> updates) {
        List<String> relationTypes = tableMapService.getRelationType(tableName);
        List<String> relatedTables = tableMapService.getRelatedTable(tableName);

        // 🛠 Güncellenecek normal kolonlar
        StringBuilder queryBuilder = new StringBuilder("UPDATE " + tableName + " SET ");
        Map<String, Object> params = new HashMap<>();

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String columnName = entry.getKey();
            Object columnValue = entry.getValue();

            if (relatedTables.contains(columnName.replace("_id", ""))) {
                int index = relatedTables.indexOf(columnName.replace("_id", ""));
                if ("many-to-many".equalsIgnoreCase(relationTypes.get(index))) {
                    // 🔹 Eğer Many-to-Many ise, önce eski ilişkileri kaldırıp yenisini ekleyelim
                    String relatedTable = relatedTables.get(index);
                    String joinTable = tableName + "_" + relatedTable;

                    String deleteManyToManyQuery = "DELETE FROM " + joinTable + " WHERE " + tableName + "_id = :id";
                    entityManager.createNativeQuery(deleteManyToManyQuery)
                            .setParameter("id", id)
                            .executeUpdate();

                    System.out.println("🔹 Many-to-Many ilişkili eski veriler silindi: " + joinTable);

                    // **Eğer yeni değer null değilse Many-to-Many ilişkisini ekleyelim**
                    if (columnValue != null) {
                        String insertManyToManyQuery = "INSERT INTO " + joinTable + " (" + tableName + "_id, " + relatedTable + "_id) VALUES (:id, :relatedId)";
                        entityManager.createNativeQuery(insertManyToManyQuery)
                                .setParameter("id", id)
                                .setParameter("relatedId", columnValue)
                                .executeUpdate();

                        System.out.println("✅ Many-to-Many ilişkisi güncellendi: " + joinTable);
                    }
                }
            } else {
                // **Many-to-Many değilse, normal kolon güncelleme işlemi**
                queryBuilder.append(columnName).append(" = :").append(columnName).append(", ");
                params.put(columnName, columnValue);
            }
        }

        // **Eğer sadece Many-to-Many güncellemesi yapılıyorsa, normal UPDATE çalıştırmaya gerek yok**
        if (!params.isEmpty()) {
            queryBuilder.setLength(queryBuilder.length() - 2);
            queryBuilder.append(" WHERE id = :id");
            params.put("id", id);

            Query updateQuery = entityManager.createNativeQuery(queryBuilder.toString());
            params.forEach(updateQuery::setParameter);
            updateQuery.executeUpdate();

            System.out.println("✅ Normal kolon güncellendi: " + tableName);
        }
    }



    /** ✅ Veri silme işlemi */
    @Transactional
    public void deleteFromTable(String tableName, Long id) {
        List<String> relationTypes = tableMapService.getRelationType(tableName);
        List<String> relatedTables = tableMapService.getRelatedTable(tableName);

        // 🛠 Önce Many-to-Many ilişkileri olan bağlantı tablosundaki kayıtları sil
        for (int i = 0; i < relationTypes.size(); i++) {
            if ("many-to-many".equalsIgnoreCase(relationTypes.get(i))) {
                String relatedTable = relatedTables.get(i);
                String joinTable = tableName + "_" + relatedTable; // Ara tablo adı

                String manyToManyDeleteQuery = "DELETE FROM " + joinTable + " WHERE " + tableName + "_id = :id";
                entityManager.createNativeQuery(manyToManyDeleteQuery)
                        .setParameter("id", id)
                        .executeUpdate();

                System.out.println("🔹 Many-to-Many ilişkili kayıtlar silindi: " + joinTable);
            }
        }

        // 🛠 Son olarak asıl tablodaki kaydı sil
        String deleteQuery = "DELETE FROM " + tableName + " WHERE id = :id";
        entityManager.createNativeQuery(deleteQuery)
                .setParameter("id", id)
                .executeUpdate();

        System.out.println("✅ Kayıt başarıyla silindi: " + tableName + " (ID: " + id + ")");
    }


    /** ✅ Many-to-One ve Many-to-Many İlişkilerini Getir */
    public List<Map<String, Object>> getForeignKeys(String tableName) {
        String mapQuery = """
        SELECT related_table AS relatedTable, relation_colum_name AS relationColumn, relation_type AS relationType
        FROM table_map 
        WHERE table_name = :tableName""";

        List<Object[]> results = entityManager.createNativeQuery(mapQuery)
                .setParameter("tableName", tableName)
                .getResultList();

        if (results.isEmpty()) {
            return List.of();
        }

        return results.stream()
                .map(row -> Map.of(
                        "relatedTable", row[0],
                        "relationColumn", row[1],
                        "relationType", row[2]
                ))
                .toList();
    }

    /** ✅ Many-to-Many Bağlantılı Verileri Getir */
    public List<Map<String, Object>> getForeignKeyData(String relatedTable, String relationColumn) {
        String dataQuery = "SELECT id, " + relationColumn + " AS value FROM " + relatedTable;

        List<Object[]> foreignKeyData = entityManager.createNativeQuery(dataQuery).getResultList();

        return foreignKeyData.stream()
                .map(row -> Map.of("id", row[0], "value", row[1]))
                .toList();
    }

    /** ✅ Belirtilen tablonun kolon isimlerini getirir. */
    public List<String> getTableColumns(String tableName) {
        String query = """
        SELECT column_name 
        FROM information_schema.columns 
        WHERE table_name = :tableName
        ORDER BY ordinal_position""";

        return entityManager.createNativeQuery(query)
                .setParameter("tableName", tableName)
                .getResultList();
    }

    /** ✅ Many-to-One ve Many-to-Many ilişkili verileri de içeren tablo verilerini getir */
    public List<Map<String, Object>> getTableData(String tableName) {
        List<String> columnNames = getTableColumns(tableName);

        String foreignKeyQuery = """
        SELECT related_table, relation_colum_name, relation_type
        FROM table_map 
        WHERE table_name = :tableName
    """;

        List<Object[]> foreignKeyResults = entityManager.createNativeQuery(foreignKeyQuery)
                .setParameter("tableName", tableName)
                .getResultList();

        Map<String, String> manyToOneMappings = new HashMap<>();
        Map<String, String> manyToManyMappings = new HashMap<>();

        for (Object[] row : foreignKeyResults) {
            String relatedTable = row[0].toString();
            String relationColumn = row[1].toString();
            String relationType = row[2].toString();

            if ("many-to-one".equalsIgnoreCase(relationType)) {
                manyToOneMappings.put(relatedTable, relationColumn);
            } else if ("many-to-many".equalsIgnoreCase(relationType)) {
                manyToManyMappings.put(relatedTable, relationColumn);
            }
        }

        // Ana tablodaki verileri çek
        String query = "SELECT * FROM " + tableName;
        List<Object[]> resultList = entityManager.createNativeQuery(query).getResultList();

        List<Map<String, Object>> formattedData = new ArrayList<>();
        for (Object[] row : resultList) {
            Map<String, Object> rowMap = new LinkedHashMap<>();

            for (int i = 0; i < columnNames.size(); i++) {
                rowMap.put(columnNames.get(i), row[i]);
            }

            // One-to-Many ilişkileri getir
            for (String relatedTable : manyToOneMappings.keySet()) {
                String relationColumn = manyToOneMappings.get(relatedTable);
                Integer foreignKeyId = (Integer) rowMap.get(relatedTable + "_id");

                if (foreignKeyId != null) {
                    String relatedQuery = "SELECT " + relationColumn + " FROM " + relatedTable + " WHERE id = :id";
                    List<Object> relatedValues = entityManager.createNativeQuery(relatedQuery)
                            .setParameter("id", foreignKeyId)
                            .getResultList();

                    if (!relatedValues.isEmpty()) {
                        rowMap.put(relatedTable, relatedValues.get(0));
                    }
                }
            }

            // Many-to-Many ilişkileri getir
            for (String relatedTable : manyToManyMappings.keySet()) {
                String joinTable = findJoinTableName(tableName, relatedTable);
                if (joinTable != null) {
                    String manyToManyQuery = "SELECT " + relatedTable + ".id, " + manyToManyMappings.get(relatedTable) +
                            " FROM " + relatedTable +
                            " JOIN " + joinTable + " ON " + relatedTable + ".id = " + joinTable + "." + relatedTable + "_id" +
                            " WHERE " + joinTable + "." + tableName + "_id = :recordId";

                    List<Object[]> relatedData = entityManager.createNativeQuery(manyToManyQuery)
                            .setParameter("recordId", rowMap.get("id"))
                            .getResultList();

                    List<String> relatedItems = new ArrayList<>();
                    for (Object[] relRow : relatedData) {
                        relatedItems.add(relRow[1].toString());
                    }

                    // **Daha iyi UI desteği için Many-to-Many ilişkili verileri liste olarak sakla**
                   rowMap.put(relatedTable , String.join(", ", relatedItems));
                }
            }

            formattedData.add(rowMap);
        }

        return formattedData;
    }


}
