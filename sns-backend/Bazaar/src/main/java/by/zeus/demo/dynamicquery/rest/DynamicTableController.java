package by.zeus.demo.dynamicquery.rest;

import by.zeus.demo.dynamicquery.service.dynamic.DynamicQueryService;
import by.zeus.demo.dynamicquery.service.dynamic.DynamicTableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/create_dynamic-table")
@CrossOrigin({"http://localhost:4401", "http://localhost:4200"})
public class DynamicTableController {

    private final DynamicTableService dynamicTableService;
    private final DynamicQueryService dynamicQueryService;

    public DynamicTableController(DynamicTableService dynamicTableService, DynamicQueryService dynamicQueryService) {
        this.dynamicTableService = dynamicTableService;
        this.dynamicQueryService = dynamicQueryService;
    }

    /** ✅ Tablodan tüm verileri getir */
    @GetMapping("/{tableName}")
    public ResponseEntity<List<Map<String, Object>>> getTableData(@PathVariable String tableName) {
        return generateResponse(() -> dynamicTableService.getTableData(tableName));
    }

    /** ✅ Many-to-Many ilişkisini ekleme */
    @PostMapping("/{joinTable}/{table1}/{table2}/{recordId}/many-to-many")
    public ResponseEntity<Map<String, Object>> insertManyToManyRelation(
            @PathVariable String joinTable,
            @PathVariable String table1,
            @PathVariable String table2,
            @PathVariable Long recordId,
            @RequestBody List<Long> relatedIds) {

        return generateResponse(() -> {
            dynamicTableService.insertManyToManyRelation(joinTable, table1, table2, recordId, relatedIds);
            return successMessage("Many-to-Many ilişkileri başarıyla eklendi.", joinTable, recordId);
        });
    }


    /** ✅ FK içeren sütunları getir */
    @GetMapping("/foreign-keys/{tableName}")
    public ResponseEntity<List<Map<String, Object>>> getForeignKeys(@PathVariable String tableName) {
        return generateResponse(() -> dynamicTableService.getForeignKeys(tableName));
    }

    /** ✅ İlgili Tablodan Foreign Key Verilerini Getir */
    @GetMapping("/foreign-key-data/{relatedTable}/{relationColumn}")
    public ResponseEntity<List<Map<String, Object>>> getForeignKeyData(
            @PathVariable String relatedTable,
            @PathVariable String relationColumn) {
        return generateResponse(() -> dynamicTableService.getForeignKeyData(relatedTable, relationColumn));
    }

    /** ✅ Yeni veri ekleme */
    @PostMapping("/{tableName}")
    public ResponseEntity<Map<String, Object>> insertIntoTable(@PathVariable String tableName, @RequestBody Map<String, Object> values) {
        return generateResponse(() -> {
            Long insertedId = dynamicTableService.insertIntoTable(tableName, values);
            return successMessage("Veri başarıyla eklendi.", tableName, insertedId);
        });
    }


    /** ✅ Var olan veriyi güncelleme */
    @PutMapping("/{tableName}/{id}")
    public ResponseEntity<Map<String, Object>> updateTable(
            @PathVariable String tableName,
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return generateResponse(() -> {
            dynamicTableService.updateTable(tableName, id, updates);
            return successMessage("Veri başarıyla güncellendi.", tableName, id);
        });
    }

    /** ✅ Veri silme işlemi */
    @DeleteMapping("/{tableName}/{id}")
    public ResponseEntity<Map<String, Object>> deleteFromTable(@PathVariable String tableName, @PathVariable Long id) {
        return generateResponse(() -> {
            dynamicTableService.deleteFromTable(tableName, id);
            return successMessage("Veri başarıyla silindi.", tableName, id);
        });
    }

    /** ✅ Kolon isimlerini döndür */
    @GetMapping("/columns/{tableName}")
    public ResponseEntity<List<String>> getTableColumns(@PathVariable String tableName) {
        return generateResponse(() -> dynamicTableService.getTableColumns(tableName));
    }

    /** ✅ Dinamik sorgulama çalıştırır */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> executeQuery(
            @RequestParam String tableName,
            @RequestParam String columnName,
            @RequestParam String value) {
        return generateResponse(() -> dynamicQueryService.executeDynamicQuery(tableName, columnName, value));
    }

    /**
     * ✅ **Genel JSON Response Wrapper Metodu**
     * Her metot için JSON formatında başarılı/başarısız dönüşler sağlar.
     */
    private <T> ResponseEntity<T> generateResponse(ResponseSupplier<T> supplier) {
        try {
            return ResponseEntity.ok(supplier.get());
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Hata: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body((T) errorResponse);
        }
    }

    /**
     * ✅ **Başarı Yanıtı Oluşturucu**
     * JSON formatında başarı yanıtları oluşturur.
     */
    private Map<String, Object> successMessage(String message, String tableName, Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", "success");
        response.put("table", tableName);
        if (id != null) response.put("id", id);
        return response;
    }

    /**
     * ✅ **Lambda Interface for Exception Handling**
     */
    @FunctionalInterface
    private interface ResponseSupplier<T> {
        T get() throws Exception;
    }
}
