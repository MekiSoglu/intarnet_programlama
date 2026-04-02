package by.zeus.demo.tablemap.service;

import by.zeus.demo.tablemap.domain.ViewMap;
import by.zeus.demo.tablemap.repository.ViewRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
public class ViewService {
    private final ViewRepository viewRepository;


    public ViewService(ViewRepository viewRepository) {
        this.viewRepository = viewRepository;
    }

    public List<String> getAll(){
        List<ViewMap> get=viewRepository.findViewMap();
        List<String> result = new ArrayList<>();
        for(ViewMap viewMap : get){
            result.add(viewMap.getViewName());

        }
        return result;
    }

    public String findView(String relationTableName){
      return viewRepository.findView(relationTableName);
    }

    public void delete(String ViewName){
        viewRepository.deleteViewByName(ViewName);
    }

    public void save(ViewMap viewMap) {
        viewRepository.saveView(viewMap.getRelationTableName(), viewMap.getViewName());
    }
}
