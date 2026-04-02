package by.zeus.demo.tablemap.web.rest;

import by.zeus.demo.tablemap.service.DynamicProcedure;
import by.zeus.demo.tablemap.service.IslemTableMapService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/islemler")
@CrossOrigin({"http://localhost:4401", "http://localhost:4200"})
public class IslemTableMapController {

    private final IslemTableMapService islemTableMapService;
    private final DynamicProcedure dynamicProcedure;

    public IslemTableMapController(IslemTableMapService islemTableMapService, final DynamicProcedure dynamicProcedure) {
        this.islemTableMapService = islemTableMapService;
        this.dynamicProcedure = dynamicProcedure;
    }

    /** ✅ Tüm işlemleri getir */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        List<Map<String, Object>> islemler = islemTableMapService.getAll();
        return ResponseEntity.ok(islemler);
    }

    /** ✅ Belirli bir işlem adını getir */
    @GetMapping("/{name}")
    public ResponseEntity<Map<String, Object>> getByIslemAdi(@PathVariable String name) {
        // URL Encoded veriyi çöz
        name = URLDecoder.decode(name, StandardCharsets.UTF_8);

        Map<String, Object> islem = islemTableMapService.getIslemByAdi(name);

        if (islem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(islem);
    }



    /** ✅ Yeni işlem oluştur */
    @PostMapping
    public ResponseEntity<String> create(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");

            // JSON verisini String'e çevir ve JsonNode nesnesine dönüştür
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonData = objectMapper.valueToTree(request.get("jsonData"));

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("İşlem adı boş olamaz!");
            }

            islemTableMapService.create(name, jsonData);
            return ResponseEntity.ok("İşlem başarıyla oluşturuldu.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Sunucu hatası: " + e.getMessage());
        }
    }


    @PostMapping("/create")
    public ResponseEntity<String> createProcedure(@RequestBody Map<String, Object> jsonData) {
        try {
            dynamicProcedure.createProcedure(jsonData);
            return ResponseEntity.ok("Prosedür başarıyla oluşturuldu.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    /** ✅ Prosedürü Çalıştır */

    @PostMapping("/execute/{procedureName}")
    public ResponseEntity<String> executeProcedure(@PathVariable String procedureName, @RequestBody Map<String, Object> params) {
        try {
            dynamicProcedure.executeProcedure(procedureName, params);
            return ResponseEntity.ok("Prosedür başarıyla çalıştırıldı.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

}
