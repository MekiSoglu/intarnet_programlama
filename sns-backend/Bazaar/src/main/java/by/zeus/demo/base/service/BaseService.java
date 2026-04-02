package by.zeus.demo.base.service;

import by.zeus.demo.base.domain.BaseEntity;
import by.zeus.demo.base.repository.BaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BaseService<E extends BaseEntity> {

    @Autowired
    private EntityService entityService;

    protected static final ExampleMatcher exampleLikeMatcher = ExampleMatcher.matching()
                                                                             .withIgnorePaths("id", "version", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate")
                                                                             .withIgnoreNullValues()
                                                                             .withIgnoreCase()
                                                                             .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);


    private final Logger log = LoggerFactory.getLogger(BaseService.class);

    private final BaseRepository<E> repository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public BaseService(
            final BaseRepository<E> repository
    ) {
        this.repository = repository;
    }

    @Cacheable
    public boolean existsById(final Long id) {
        return repository.existsById(id);
    }


    /**
     * Get one attribute by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Cacheable
    public Optional<E> findOne(final Long id) {
        log.debug("Request to get {} : {}", getEntityClassName(), id);
        return repository.findById(id);
    }

    /**
     * Get one attribute by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Cacheable
    public E getOneOrFail(final Long id) {
        log.debug("Request to get {} : {}", getEntityClassName(), id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cannot find " + getEntityClassName() + " with id: " + id));
    }

    /**
     * Get one attribute by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public E findOneOrFailNoCache(final Long id) {
        log.debug("Request to get {} : {}", getEntityClassName(), id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cannot find " + getEntityClassName() + " with id: " + id));
    }

    /**
     * Get one attribute by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public E getOneForUpdateOrFail(final Long id) {
        log.debug("Request to get {} : {}", getEntityClassName(), id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cannot find " + getEntityClassName() + " with id: " + id));
    }

    /**
     * Save a attribute.
     *
     * @param model the entity to save.
     * @return the persisted entity.
     */
    @CacheEvict
    public E create(final E model) {
        if (model.getId() != null) {
        }
        log.debug("Request to save {}", getEntityClassName());

        return repository.save(model);
    }

    @CacheEvict
    public void deleteAll(){
        repository.deleteAll();
    }

    public E attach(final E model) {
        log.debug("Request to attach {} : {}", getEntityClassName(), model.getId());
        return repository.attach(model);
    }

    /**
     * Update a attribute.
     *
     * @param model the entity to save.
     * @return the persisted entity.
     */
    @CacheEvict
    public E update(final E model) {
        if (!existsById(model.getId())) {
        }

        log.debug("Request to update {} : {}", getEntityClassName(), model.getId());
        return repository.save(model);
    }

    /**
     * Get all the attributes.
     *
     * @return the list of entities.
     */
    public List<E> findAll() {
        log.debug("Request to get all {}", getEntityClassName());
        return repository.findAll();
    }

    /**
     * Get all the entities by ids.
     *
     * @return the list of entities.
     */
    @Cacheable
    public Set<E> findAll(final Set<Long> ids) {
        log.debug("Request to get all {} with ids: {}", getEntityClassName(), ids);
        return repository.findAllByIdIn(ids);
    }

    @Cacheable
    public Page<E> findAll(final Pageable pageable) {
        log.debug("Request to get all {} paged", getEntityClassName());
        return repository.findAll(pageable);
    }

    @Cacheable
    public List<E> findAllByExampleLike(final E entity) {
        log.debug("Request to get all matching {} like example {}", getEntityClassName(), entity);
        final Example<E> example = Example.of(entity, exampleLikeMatcher);
        return repository.findAll(example);
    }

    @Cacheable
    public Page<E> findAllByExampleLikePaged(final E entity, final Pageable pageable) {
        log.debug("Request to get all matching {} like example {} paged", getEntityClassName(), entity);
        final Example<E> example = Example.of(entity, exampleLikeMatcher);
        return repository.findAll(example, pageable);
    }

    /**
     * Get one entity by id with eager relations.
     *
     * @param id        the id of the entity.
     * @param relations the related field names of the entity.
     * @return the entity.
     */
    @Cacheable
    public Optional<E> findOneWithEagerRelations(final Long id, final Set<String> relations) {
        log.debug("Request to get {} : {} with eager relations: {}", getEntityClassName(), id, relations);
        return repository.findByIdWithEagerRelations(id, relations);
    }

    /**
     * Get one entity by id with eager relations.
     *
     * @param ids       the id list of the entity.
     * @param relations the related field names of the entity.
     * @return the entity.
     */
    @Cacheable
    public List<E> findAllByIdsInWithEagerRelations(final List<Long> ids, final Set<String> relations) {
        log.debug("Request to get {} : {} with eager relations: {}", getEntityClassName(), ids, relations);
        return repository.findAllByIdsInWithEagerRelations(ids, relations);
    }

    /**
     * Delete the attribute by id.
     *
     * @param id the id of the entity.
     */
    @CacheEvict(allEntries = true)
    public void delete(final Long id) {
        log.debug("Request to delete {} : {}", getEntityClassName(), id);
        repository.deleteById(id);
    }

    public EntityService getEntityService() {
        return entityService;
    }

    public BaseRepository<E> getRepository() {
        return repository;
    }

    public String getEntityClassName() {
        return getEntityClass().getSimpleName();
    }

    public Class<E> getEntityClass() {
        return getRepository().getEntityClass();
    }
}

