package by.zeus.demo.tablemap.web.rest;


import by.zeus.demo.tablemap.service.DynamicProcedure;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/procedures")
public class DynamicProcedureController {

    private final DynamicProcedure dynamicProcedure;

    public DynamicProcedureController(DynamicProcedure dynamicProcedure) {
        this.dynamicProcedure = dynamicProcedure;
    }

    /** ✅ Prosedürü Oluştur */
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
