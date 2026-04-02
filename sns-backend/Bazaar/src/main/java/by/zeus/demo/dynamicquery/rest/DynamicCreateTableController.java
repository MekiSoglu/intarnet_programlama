package by.zeus.demo.dynamicquery.rest;


import by.zeus.demo.dynamicquery.service.dynamic.DynamicCreateTable;
import by.zeus.demo.dynamicquery.service.dynamic.DynamicQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dynamic-tables")
@CrossOrigin( {"http://localhost:4401", "http://localhost:4200"} )

public class DynamicCreateTableController {

    private final DynamicCreateTable dynamicCreateTable;

    private final DynamicQueryService dynamicQueryService;

    public DynamicCreateTableController(DynamicCreateTable dynamicCreateTable, DynamicQueryService dynamicQueryService) {
        this.dynamicCreateTable = dynamicCreateTable;
        this.dynamicQueryService = dynamicQueryService;
    }


    @GetMapping("/columns/{tableName}")
    public ResponseEntity<List<String>> getTableColumns(@PathVariable String tableName) {
        try {
            List<String> columns = dynamicCreateTable.getTableColumns(tableName);
            return ResponseEntity.ok(columns);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }



    // ✅ Dinamik Tablo Oluşturma
    @PostMapping("/create-table")
    public ResponseEntity<Map<String, Object>> createTable(@RequestBody Map<String, Object> requestData) {
        try {
            String tableName = (String) requestData.get("tableName");
            List<Map<String, String>> columns = (List<Map<String, String>>) requestData.get("columns");
            List<Map<String, String>> foreignKeys = (List<Map<String, String>>) requestData.get("foreignKeys");
            Boolean enableAlarm = (Boolean) requestData.get("enableAlarm");

            // ✅ Eğer tablo zaten varsa hata döndür
            if (dynamicCreateTable.tableExists(tableName)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Table '" + tableName + "' already exists."
                ));
            }

            // ✅ Tabloyu oluştur
            dynamicCreateTable.createTable(tableName, columns, foreignKeys, enableAlarm);

            // ✅ Oluşturulan tablonun bilgilerini JSON olarak döndür
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Table '" + tableName + "' created successfully.");
            response.put("tableName", tableName);
            response.put("columns", columns);
            response.put("foreignKeys", foreignKeys);
            response.put("enableAlarm", enableAlarm);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            ));
        }
    }


    // ✅ Yeni Sütun Ekleme
    @PostMapping("/add-column")
    public ResponseEntity<String> addColumnToTable(@RequestBody Map<String, String> requestData) {
        try {
            String tableName = requestData.get("tableName");
            String columnName = requestData.get("columnName");
            String columnType = requestData.get("columnType");

            dynamicCreateTable.addColumnToTable(tableName, columnName, columnType);
            return ResponseEntity.ok("Column '" + columnName + "' added to table '" + tableName + "'.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ✅ Sütun Silme
    @DeleteMapping("/drop-column")
    public ResponseEntity<String> dropColumnFromTable(@RequestParam String tableName, @RequestParam String columnName) {
        try {
            dynamicCreateTable.dropColumnFromTable(tableName, columnName);
            return ResponseEntity.ok("Column '" + columnName + "' removed from table '" + tableName + "'.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ✅ Tablo Silme
    @DeleteMapping("/drop-table")
    public ResponseEntity<String> dropTable(@RequestParam String tableName) {
        try {
            String result = dynamicCreateTable.dropTable(tableName);
            return ResponseEntity.ok(result); // ✅ Artık frontend doğru yanıt alacak
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }



    @GetMapping("/list-tables")
    public ResponseEntity<List<String>> listTables() {
        try {
            List<String> tables = dynamicCreateTable.listAllTables();
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

