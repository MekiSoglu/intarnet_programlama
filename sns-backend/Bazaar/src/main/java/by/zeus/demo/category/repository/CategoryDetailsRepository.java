package by.zeus.demo.category.repository;

import by.zeus.demo.category.domain.CategoryDetailsEntity;
import by.zeus.demo.base.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin( "http://localhost:4200" )
public interface CategoryDetailsRepository extends BaseRepository<CategoryDetailsEntity> {

    @Query("SELECT c FROM CategoryDetailsEntity c WHERE c.id IN (:Ids)")
    List<CategoryDetailsEntity> findCategoryDetailsBy(List<Long> Ids);

}
