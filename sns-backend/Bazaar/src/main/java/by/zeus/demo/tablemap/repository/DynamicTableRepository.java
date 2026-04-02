package by.zeus.demo.tablemap.repository;

import by.zeus.demo.base.repository.BaseRepository;
import by.zeus.demo.tablemap.domain.DynamicTableEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DynamicTableRepository extends BaseRepository<DynamicTableEntity> {
    @Query(value = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'", nativeQuery = true)
    List<String> findAllTableNames();
}
