package by.zeus.demo.tablemap.web.rest;

import by.zeus.demo.tablemap.domain.DynamicTableEntity;
import by.zeus.demo.tablemap.service.DynamicTableCrudService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dynamic_table")
@CrossOrigin( {"http://localhost:4401", "http://localhost:4200"} )
public class DynamicTableCrudController {
    private final DynamicTableCrudService dynamicTableCrudService;

    public DynamicTableCrudController(final DynamicTableCrudService dynamicTableCrudService) {
        this.dynamicTableCrudService = dynamicTableCrudService;
    }

    /**
     * ✅ Tüm dinamik tablo isimlerini getir.
     */
    @GetMapping("/list-tables")
    public List<String> getAllTableNames(){
        return dynamicTableCrudService.getAllTableNames();
    }

    @GetMapping("/getCrateTableName")
    public List<DynamicTableEntity> getAllTableName(){
        return dynamicTableCrudService.findAll();
    }
}
