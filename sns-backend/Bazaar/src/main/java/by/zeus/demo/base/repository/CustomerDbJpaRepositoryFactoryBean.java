package by.zeus.demo.base.repository;

import by.zeus.demo.base.domain.BaseEntity;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class CustomerDbJpaRepositoryFactoryBean<R extends JpaRepositoryImplementation<T, ID>, T extends BaseEntity, ID> extends JpaRepositoryFactoryBean<R, T
        , ID> {

    public CustomerDbJpaRepositoryFactoryBean(final Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(final EntityManager entityManager) {
        return new CustomerDbJpaRepositoryFactory<T, ID>(entityManager);
    }
}
