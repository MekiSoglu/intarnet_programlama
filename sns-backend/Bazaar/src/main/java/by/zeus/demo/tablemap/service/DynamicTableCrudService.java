package by.zeus.demo.tablemap.service;

import by.zeus.demo.base.repository.BaseRepository;
import by.zeus.demo.base.service.BaseService;
import by.zeus.demo.tablemap.domain.DynamicTableEntity;
import by.zeus.demo.tablemap.repository.DynamicTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DynamicTableCrudService extends BaseService<DynamicTableEntity> {
    private final DynamicTableRepository dynamicTableRepository;

    public DynamicTableCrudService(final BaseRepository<DynamicTableEntity> repository, final DynamicTableRepository dynamicTableRepository) {
        super(repository);
        this.dynamicTableRepository = dynamicTableRepository;
    }

    /**
     * ✅ Veritabanında kayıtlı tüm dinamik tabloların isimlerini döndürür.
     */
    public List<String> getAllTableNames() {
        return dynamicTableRepository.findAllTableNames();
    }
}
