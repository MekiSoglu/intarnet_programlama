package by.zeus.demo.category.service;

import by.zeus.demo.base.repository.BaseRepository;
import by.zeus.demo.category.domain.CategoryDetailsEntity;
import by.zeus.demo.category.repository.CategoryDetailsRepository;
import by.zeus.demo.base.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryDetailsService extends BaseService<CategoryDetailsEntity> {


    public CategoryDetailsService(BaseRepository<CategoryDetailsEntity> repository) {
        super(repository);

    }

    public List<CategoryDetailsEntity> findAll(List<Long> Ids){
        return getRepository().findCategoryDetailsBy(Ids);
    }



    @Override
    public CategoryDetailsRepository getRepository(){
        return (CategoryDetailsRepository) super.getRepository();
    }


}
