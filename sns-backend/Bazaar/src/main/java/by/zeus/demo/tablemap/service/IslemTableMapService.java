package by.zeus.demo.tablemap.service;

import by.zeus.demo.tablemap.domain.IslemTableMap;
import by.zeus.demo.tablemap.repository.IslemTableMapRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IslemTableMapService {

    private final IslemTableMapRepository islemTableMapRepository;

    public IslemTableMapService(IslemTableMapRepository islemTableMapRepository) {
        this.islemTableMapRepository = islemTableMapRepository;
    }

    /** ✅ Tüm işlemleri getir */
    public List<Map<String, Object>> getAll() {
        return islemTableMapRepository.findAll().stream().map(islem -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", islem.getId());
            result.put("islemAdi", islem.getIslemAdi());
            result.put("islemJson", islem.getIslemJson()); // Direkt JsonNode olarak ekledik
            return result;
        }).collect(Collectors.toList());
    }

    /** ✅ Belirli bir işlem adını getir */
    public Map<String, Object> getIslemByAdi(String islemAdi) {
        IslemTableMap islemTableMap = islemTableMapRepository.findByIslemAdi(islemAdi);

        if (islemTableMap == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", islemTableMap.getId());
        result.put("islemAdi", islemTableMap.getIslemAdi());
        result.put("islemJson", islemTableMap.getIslemJson());

        return result;
    }

    /** ✅ Yeni işlem oluştur */
    public void create(String name, JsonNode jsonData) {
        IslemTableMap islemTableMap = new IslemTableMap();
        islemTableMap.setIslemAdi(name);
        islemTableMap.setIslemJson(jsonData);

        islemTableMapRepository.save(islemTableMap);
    }
}
