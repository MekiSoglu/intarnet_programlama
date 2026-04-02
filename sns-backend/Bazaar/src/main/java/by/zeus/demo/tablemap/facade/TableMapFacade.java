package by.zeus.demo.tablemap.facade;

import by.zeus.demo.base.facade.BaseFacade;
import by.zeus.demo.base.service.BaseService;
import by.zeus.demo.base.web.mapper.BaseMapper;
import by.zeus.demo.tablemap.domain.TableMapEntity;
import by.zeus.demo.tablemap.web.dto.TableMapDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TableMapFacade extends BaseFacade<TableMapDTO , TableMapEntity> {
    public TableMapFacade(final BaseService<TableMapEntity> service,
                          final BaseMapper<TableMapDTO, TableMapEntity> mapper) {
        super(service, mapper);
    }

    @Override
    public Class<TableMapDTO> getDtoClass() {
        return null;
    }
}
