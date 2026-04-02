package by.zeus.demo.base.repository;

import by.zeus.demo.base.domain.BaseEntity;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

public class CustomerDbJpaRepositoryFactory<T extends BaseEntity, ID> extends JpaRepositoryFactory {

    public CustomerDbJpaRepositoryFactory(final EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(final RepositoryInformation information, final EntityManager entityManager) {
        //noinspection unchecked
        return new SolidRepositoryImpl<>((Class<T>) information.getDomainType(), entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(final RepositoryMetadata metadata) {
        return SolidRepository.class;
    }
}
