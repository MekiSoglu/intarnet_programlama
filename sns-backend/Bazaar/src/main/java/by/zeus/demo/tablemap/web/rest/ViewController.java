package by.zeus.demo.tablemap.web.rest;

import by.zeus.demo.tablemap.domain.ViewMap;
import by.zeus.demo.tablemap.service.ViewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/join")
@CrossOrigin( {"http://localhost:4401", "http://localhost:4200"} )
public class ViewController {
    private final ViewService viewService;

    public ViewController(ViewService viewService) {
        this.viewService = viewService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getCariler() {
        return ResponseEntity.ok(viewService.getAll());
    }


}
