package by.zeus.demo.base.facade;

import by.zeus.demo.base.domain.BaseEntity;
import by.zeus.demo.base.service.BaseService;
import by.zeus.demo.base.web.dto.BaseDTO;
import by.zeus.demo.base.web.mapper.BaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public abstract class BaseFacade<D extends BaseDTO, E extends BaseEntity> {

    private final Logger log = LoggerFactory.getLogger(BaseFacade.class);

    private final BaseService<E> service;
    private final BaseMapper<D, E> mapper;

    public BaseFacade(final BaseService<E> service, final BaseMapper<D, E> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Transactional
    public D save(final D dto) {
        log.debug("Request to save {} : {}", getEntityClassName(), dto.getId());
        final E entity = mapper.toEntity(dto);
        return mapper.toDto(service.create(entity));
    }

    @Transactional
    public Optional<D> partialUpdate(final D dto) {
        log.debug("Request to partially update {} : {}", getEntityClassName(), dto);
        return getService().findOne(dto.getId())
                           .map(existingEntity -> {
                               mapper.partialUpdate(existingEntity, dto);
                               return existingEntity;
                           })
                           .map(service::update)
                           .map(mapper::toDto);
    }

    @Transactional
    public D update(final D dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("Entity must have an id");
        }
        final E entity = mapper.toEntity(dto);
        return mapper.toDto(service.update(entity));
    }

    public List<D> findAll() {
        log.debug("Request to get all {}", getEntityClassName());
        return service.findAll().stream().map(mapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public List<D> findAll(final Set<Long> ids) {
        log.debug("Request to get all {} with ids: {}", getEntityClassName(), ids);
        return service.findAll(ids).stream().map(mapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<D> findAll(final Pageable pageable) {
        log.debug("Request to get all {} paged", getEntityClassName());
        final Page<E> page = service.findAll(pageable);
        return page.map(mapper::toDto);
    }

    public List<D> findAllByExampleLike(final D dto) {
        final E entity = mapper.toEntity(dto);
        log.debug("Request to get all matching {} like example {}", getEntityClassName(), entity);
        return service.findAllByExampleLike(entity).stream().map(mapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Optional<D> findOneWithEagerRelations(final Long id, final Set<String> relations) {
        log.debug("Request to get {} : {} with eager relations: {}", getEntityClassName(), id, relations);
        return service.findOneWithEagerRelations(id, relations).map(mapper::toDto);
    }

    public List<D> findAllByIdsInWithEagerRelations(final List<Long> ids, final Set<String> relations) {
        log.debug("Request to get {} : {} with eager relations: {}", getEntityClassName(), ids, relations);
        return service.findAllByIdsInWithEagerRelations(ids, relations).stream().map(mapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Optional<D> findOne(final Long id) {
        log.debug("Request to get {} : {}", getEntityClassName(), id);
        return service.findOne(id).map(mapper::toDto);
    }

    public D getOneOrFail(final Long id) {
        log.debug("Request to get {} : {}", getEntityClassName(), id);
        return mapper.toDto(service.getOneOrFail(id));
    }

    public void delete(final Long id) {
        log.debug("Request to delete {} : {}", getEntityClassName(), id);
        service.delete(id);
    }

    public BaseService<E> getService() {
        return service;
    }


    public BaseMapper<D, E> getMapper() {
        return mapper;
    }

    public String getEntityClassName() {
        return getService().getEntityClassName();
    }

    public Class<E> getEntityClass() {
        return getService().getEntityClass();
    }

    public abstract Class<D> getDtoClass();
}
