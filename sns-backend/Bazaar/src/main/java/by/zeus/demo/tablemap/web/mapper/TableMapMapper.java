package by.zeus.demo.tablemap.web.mapper;

import by.zeus.demo.base.web.mapper.BaseMapper;
import by.zeus.demo.tablemap.domain.TableMapEntity;
import by.zeus.demo.tablemap.web.dto.TableMapDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface TableMapMapper extends BaseMapper<TableMapDTO, TableMapEntity> {
}
