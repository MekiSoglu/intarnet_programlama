package by.zeus.demo.tablemap.repository;

import by.zeus.demo.base.repository.BaseRepository;
import by.zeus.demo.tablemap.domain.TableMapEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin( "http://localhost:4200" )

public interface TableMapRepository extends BaseRepository<TableMapEntity> {

    @Query("select tm.relationType from TableMapEntity tm where tm.tableName = :tableName")
    List<String> getByType(@Param("tableName") String tableName);

    @Query("select tm.relatedTable from TableMapEntity tm where tm.tableName = :tableName")
    List<String> getByRelatedTable(@Param("tableName") String tableName);

}
