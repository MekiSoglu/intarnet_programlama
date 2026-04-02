package by.zeus.demo.tablemap.repository;

import by.zeus.demo.tablemap.domain.ViewMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin("http://localhost:4200")
@Repository
public interface ViewRepository extends JpaRepository<ViewMap, Long> { // ✅ JpaRepository'den extend ettik

    @Query(value = "SELECT * FROM view_map", nativeQuery = true)
    List<ViewMap> findViewMap();

    @Query(value = "SELECT wp.view_name FROM view_map wp WHERE wp.relation_table_name = :tableName", nativeQuery = true)
    String findView(@Param("tableName") String relationTableName);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO view_map (relation_table_name, view_name) VALUES (:relationTableName, :viewName)", nativeQuery = true)
    void saveView(@Param("relationTableName") String relationTableName, @Param("viewName") String viewName);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM view_map WHERE view_name = :viewName", nativeQuery = true)
    void deleteViewByName(@Param("viewName") String viewName);


}
