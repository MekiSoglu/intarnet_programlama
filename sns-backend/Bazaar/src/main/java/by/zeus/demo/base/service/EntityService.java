package by.zeus.demo.base.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EntityService {

    @PersistenceContext
    private EntityManager entityManager;

    public <T> T getById(final Class<T> clazz, final long id) {
        return entityManager.find(clazz, id);
    }

    public <T> T persist(final T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public <T> T detach(final T entity) {
        entityManager.detach(entity);
        return entity;
    }

    public void flush() {
        entityManager.flush();
    }
}

