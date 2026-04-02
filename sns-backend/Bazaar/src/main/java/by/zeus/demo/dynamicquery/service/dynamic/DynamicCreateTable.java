package by.zeus.demo.dynamicquery.service.dynamic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DynamicCreateTable {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void createTable(String tableName, List<Map<String, String>> columns, List<Map<String, String>> foreignKeys, boolean enableAlarm) {
        if (tableExists(tableName)) {
            throw new IllegalArgumentException("Table " + tableName + " already exists.");
        }

        // **1️⃣ Önce Ana Tabloyu Kur**
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        queryBuilder.append(normalizeColumnName(tableName))
                .append(" (id SERIAL PRIMARY KEY, created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ");

        for (Map<String, String> column : columns) {
            String columnName = normalizeColumnName(column.get("key"));
            String columnType = mapColumnType(column.get("type"));
            queryBuilder.append(columnName).append(" ").append(columnType).append(", ");
        }

        if (enableAlarm) {
            boolean hasStartDate = columns.stream().anyMatch(c -> "start_date".equalsIgnoreCase(c.get("key")));
            boolean hasEndDate = columns.stream().anyMatch(c -> "end_date".equalsIgnoreCase(c.get("key")));

            if (!hasStartDate) {
                queryBuilder.append("start_date DATE, ");
            }
            if (!hasEndDate) {
                queryBuilder.append("end_date DATE, ");
            }
            saveAlarmTableName(tableName);
        }

        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(");");

        System.out.println("🛠️ Oluşturulan SQL Sorgusu (Ana Tablo): " + queryBuilder);
        entityManager.createNativeQuery(queryBuilder.toString()).executeUpdate();
        addedTableName(tableName);

        // **2️⃣ Foreign Key'leri İşle**
        if (foreignKeys != null && !foreignKeys.isEmpty()) {
            for (Map<String, String> foreignKey : foreignKeys) {
                String columnName = normalizeColumnName(foreignKey.get("column"));
                String referencedTable = normalizeColumnName(foreignKey.get("references"));
                String relationType = foreignKey.get("relation");
                String relationColumName = foreignKey.get("relationColumn");

                // **2.1 One-to-Many İlişkisi (FK ekleme)**
                if ("one-to-many".equalsIgnoreCase(relationType)) {
                    // Eğer bağlı tablo yoksa, önce bağlı tabloyu oluştur
                    if (!tableExists(referencedTable)) {
                        createEmptyTable(referencedTable);
                    }

                    String alterQuery = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " INTEGER REFERENCES " + referencedTable + "(id)";
                    entityManager.createNativeQuery(alterQuery).executeUpdate();
                    saveTableRelation(tableName, referencedTable, relationType, relationColumName);
                }
                // **2.2 Many-to-Many İlişkisi (Ara Tablo oluşturma)**
                else if ("many-to-many".equalsIgnoreCase(relationType)) {
                    // Eğer bağlı tablo yoksa, önce bağlı tabloyu oluştur
                    if (!tableExists(referencedTable)) {
                        createEmptyTable(referencedTable);
                    }

                    // **Önce Ara Tabloyu Kur**
                    String joinTable = tableName + "_" + referencedTable;
                    createJoinTable(joinTable, tableName, referencedTable);
                    saveTableRelation(tableName, referencedTable, relationType, relationColumName);
                }
            }
        }
    }


    @Transactional
    public void createEmptyTable(String tableName) {
        if (!tableExists(tableName)) {
            String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (id SERIAL PRIMARY KEY, created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
            entityManager.createNativeQuery(query).executeUpdate();
            addedTableName(tableName);
            System.out.println("✅ Boş tablo oluşturuldu: " + tableName);
        }
    }



    public List<String> getTableColumns(String tableName) {
        String query = "SELECT column_name FROM information_schema.columns WHERE table_name = :tableName";
        return entityManager.createNativeQuery(query)
                            .setParameter("tableName", tableName)
                            .getResultList();
    }



    private String normalizeColumnName(String columnName) {
        // Türkçe karakterleri İngilizce karşılıklarıyla değiştir
        columnName = columnName.toLowerCase()
                               .replace("ı", "i")
                               .replace("ğ", "g")
                               .replace("ü", "u")
                               .replace("ş", "s")
                               .replace("ö", "o")
                               .replace("ç", "c")
                               .replace("İ", "i")
                               .replace("Ğ", "g")
                               .replace("Ü", "u")
                               .replace("Ş", "s")
                               .replace("Ö", "o")
                               .replace("Ç", "c");

        // Boşlukları ve özel karakterleri alt çizgiyle değiştir
        columnName = columnName.replaceAll("[^a-zA-Z0-9]", "_");

        // Eğer sütun adı alt çizgiyle bitiyorsa, bunu kaldır
        if (columnName.endsWith("_")) {
            columnName = columnName.substring(0, columnName.length() - 1);
        }

        return columnName;
    }







    @Transactional
    public void createJoinTable(String joinTable, String table1, String table2) {
        String query = "CREATE TABLE IF NOT EXISTS " + joinTable + " (" +
                table1 + "_id INTEGER REFERENCES " + table1 + "(id), " +
                table2 + "_id INTEGER REFERENCES " + table2 + "(id), " +
                "PRIMARY KEY (" + table1 + "_id, " + table2 + "_id)" +
                ");";
        entityManager.createNativeQuery(query).executeUpdate();
    }



    @Transactional
    public void addColumnToTable(String tableName, String columnName, String columnType) {
        if (!columnExists(tableName, columnName)) {
            String query = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + mapColumnType(columnType);
            entityManager.createNativeQuery(query).executeUpdate();
        }
    }

    @Transactional
    public void dropColumnFromTable(String tableName, String columnName) {
        if (columnExists(tableName, columnName)) {
            String query = "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
            entityManager.createNativeQuery(query).executeUpdate();
        }
    }

    @Transactional
    public String dropTable(String tableName) {
        if (tableExists(tableName)) {
            String query = "DROP TABLE IF EXISTS " + tableName;
            entityManager.createNativeQuery(query).executeUpdate();

            String query2 = "DELETE FROM dynamic_tables WHERE table_name = :tableName";
            entityManager.createNativeQuery(query2)
                         .setParameter("tableName", tableName)
                         .executeUpdate();

            return "Table '" + tableName + "' deleted successfully.";
        }
        return "Table '" + tableName + "' does not exist.";
    }


    public boolean tableExists(String tableName) {
        String query = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = :tableName)";
        Boolean exists = (Boolean) entityManager.createNativeQuery(query)
                                                .setParameter("tableName", tableName)
                                                .getSingleResult();
        return exists;
    }

    public boolean columnExists(String tableName, String columnName) {
        String query = "SELECT EXISTS (SELECT FROM information_schema.columns WHERE table_name = :tableName AND column_name = :columnName)";
        Boolean exists = (Boolean) entityManager.createNativeQuery(query)
                                                .setParameter("tableName", tableName)
                                                .setParameter("columnName", columnName)
                                                .getSingleResult();
        return exists;
    }

    private String mapColumnType(String type) {
        return switch (type.toLowerCase()) {
            case "number" -> "INTEGER";
            case "varchar" -> "VARCHAR(255)";
            case "date" -> "DATE";
            case "boolean" -> "BOOLEAN";
            case "json" -> "JSONB";
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }

    @Transactional
    public void saveTableRelation(String tableName, String relatedTable, String relationType, String relationColumnName) {
        String query = "INSERT INTO table_map (table_name, related_table, relation_type, relation_colum_name, created_by, version) VALUES (:tableName, :relatedTable, :relationType, :relationColumnName, :createdBy, :version)";
        entityManager.createNativeQuery(query)
                     .setParameter("tableName", tableName)
                     .setParameter("relatedTable", relatedTable)
                     .setParameter("relationType", relationType)
                     .setParameter("relationColumnName", relationColumnName)
                     .setParameter("createdBy", "system")
                     .setParameter("version", 1)
                     .executeUpdate();
    }


    @Transactional
    public void addedTableName(String tableName){
        String query = "INSERT INTO dynamic_tables (table_name, created_by , version) VALUES (:tableName, :createdBy , :version)";
        entityManager.createNativeQuery(query)
                     .setParameter("tableName", tableName)
                     .setParameter("createdBy", "system")// Varsayılan kullanıcı adı
                     .setParameter("version",1)
                     .executeUpdate();
    }

    @Transactional
    public void saveAlarmTableName(String tableName) {
        String query = "INSERT INTO table_alarm (table_name) VALUES (:tableName)";
        entityManager.createNativeQuery(query)
                     .setParameter("tableName", tableName)
                     .executeUpdate();
    }


    // **✅ Tüm Dinamik Tabloları Listeleme**
    public List<String> listAllTables() {
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        return entityManager.createNativeQuery(query).getResultList();
    }
}
