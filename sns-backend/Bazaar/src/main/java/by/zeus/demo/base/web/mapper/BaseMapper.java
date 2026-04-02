package by.zeus.demo.base.web.mapper;


import by.zeus.demo.base.domain.BaseEntity;
import by.zeus.demo.base.web.dto.BaseDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contract for a generic dto to entity mapper.
 *
 * @param <D> - DTO type parameter.
 * @param <E> - Entity type parameter.
 */

public interface BaseMapper<D extends BaseDTO, E extends BaseEntity> {

    E toEntity(D dto);

    D toDto(E entity);

    Set<E> toEntity(Set<D> dtoList);

    default Set<D> toDto(final Set<E> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return Collections.emptySet();
        }
        return entityList.stream().map(this::toDto).collect(Collectors.toSet());
    }

    default List<D> toListDto(final Set<E> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<E> toListEntity(final List<D> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget E entity, D dto);

    default Set<String> getEagerRelationsForUpdate() {
        return Collections.emptySet();
    }

}