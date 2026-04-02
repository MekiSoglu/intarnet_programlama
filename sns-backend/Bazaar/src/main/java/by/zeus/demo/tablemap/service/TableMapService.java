package by.zeus.demo.tablemap.service;

import by.zeus.demo.base.repository.BaseRepository;
import by.zeus.demo.base.service.BaseService;
import by.zeus.demo.tablemap.domain.TableMapEntity;
import by.zeus.demo.tablemap.repository.TableMapRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableMapService extends BaseService<TableMapEntity> {
    private final TableMapRepository repository;
    public TableMapService(final BaseRepository<TableMapEntity> repository, TableMapRepository repository1) {
        super(repository);
        this.repository = repository1;
    }

    public List<String> getRelationType(String tableName){
        return repository.getByType(tableName);
    }

    public List<String> getRelatedTable(String tableName){
        return repository.getByRelatedTable(tableName);
    }


}
