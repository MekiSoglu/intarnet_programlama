package by.zeus.demo.dynamicquery.rest;


import by.zeus.demo.dynamicquery.service.dynamic.DynamicJoinTable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dynamic-join")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4401"})
public class DynamicJoinTableController {

    private final DynamicJoinTable dynamicJoinTable;

    public DynamicJoinTableController(DynamicJoinTable dynamicJoinTable) {
        this.dynamicJoinTable = dynamicJoinTable;
    }

    /**
     * ✅ 1️⃣ Tabloların ilişkilerini getir
     * @param tableNames List<String> -> İlişkileri alınacak tablolar listesi
     * @return ResponseEntity<List<Map<String, Object>>> -> İlişkili tabloların bilgileri (table_name, related_table, relation_type)
     */
    @PostMapping("/relations")
    public ResponseEntity<List<Map<String, Object>>> getTableRelations(@RequestBody List<String> tableNames) {
        List<Map<String, Object>> relations = dynamicJoinTable.getTableRelations(tableNames);
        return ResponseEntity.ok(relations);
    }

    /**
     * ✅ 2️⃣ Dinamik View oluşturma
     * @param request Map<String, Object> -> tableNames, relations, viewName içeren JSON isteği
     * @return ResponseEntity<String> -> Oluşturulan View adı
     */
    @PostMapping("/create-view")
    public ResponseEntity<String> createDynamicView(@RequestBody Map<String, Object> request) {
        List<String> tableNames = (List<String>) request.get("tableNames");
        List<Map<String, Object>> relations = (List<Map<String, Object>>) request.get("relations");
        String viewName = request.get("viewName").toString();

        String createdView = dynamicJoinTable.createDynamicView(tableNames, relations, viewName);
        return ResponseEntity.ok(createdView);
    }

    /**
     * ✅ 3️⃣ Belirtilen View'den veriyi getir
     * @param viewName String -> Kullanılacak View adı
     * @return ResponseEntity<List<Map<String, Object>>> -> View'den çekilen veriler
     */
    @GetMapping("/fetch/{viewName}")
    public ResponseEntity<List<Map<String, Object>>> fetchDataFromView(@PathVariable String viewName) {
        List<Map<String, Object>> data = dynamicJoinTable.fetchDataFromView(viewName);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/fetch/{viewName}/filter")
    public ResponseEntity<List<Map<String, Object>>> fetchFilteredData(
            @PathVariable String viewName,
            @RequestParam String columnName,
            @RequestParam String columnValue) {

        List<Map<String, Object>> data = dynamicJoinTable.fetchFilteredDataFromView(viewName, columnName, columnValue);
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/delete/{viewName}")
    public ResponseEntity<Map<String, String>> deleteCari(@PathVariable String viewName) {
        return dynamicJoinTable.deleteCari(viewName);
    }

}
