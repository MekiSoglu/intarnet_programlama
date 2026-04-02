package by.zeus.demo.base.repository;

import by.zeus.demo.base.domain.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SolidRepositoryImpl<T extends BaseEntity, ID> extends SimpleJpaRepository<T, ID> implements SolidRepository<T, ID> {

    private final EntityManager entityManager;

    public SolidRepositoryImpl(final Class<T> domainClass, final EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }


    @Override
    public void detach(final T entity) {
        entityManager.detach(entity);
    }

    @Override
    public T attach(final T entity) {
        if (entityManager.contains(entity)) {
            return entity;
        }
        if (entity.getId() == null) {
            throw new IllegalStateException("Entity ID is null, cannot be attached!");
        }

        return entityManager.merge(entity);
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public CriteriaQuery<T> criteriaQuery() {
        return entityManager.getCriteriaBuilder().createQuery(getEntityClass());
    }

    @Override
    public Class<T> getEntityClass() {
        return super.getDomainClass();
    }

    @Override
    public List<T> findAllByIdsInWithEagerRelations(final List<Long> ids, final Set<String> relations) {
        final String joinFetches = relations.stream().map(r -> "left join fetch e." + r).collect(Collectors.joining(" "));

        return entityManager
                .createQuery(
                        "select e from " + getEntityClass().getSimpleName() + " e " + joinFetches + " where e.id in :ids",
                        getEntityClass()
                )
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public Optional<T> findByIdWithEagerRelations(final Long id, final Set<String> relations) {
        return findAllByIdsInWithEagerRelations(Collections.singletonList(id), relations).stream().findFirst();
    }

}
