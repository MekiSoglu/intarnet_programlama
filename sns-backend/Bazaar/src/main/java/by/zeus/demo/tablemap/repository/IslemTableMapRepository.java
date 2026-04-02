package by.zeus.demo.tablemap.repository;


import by.zeus.demo.tablemap.domain.IslemTableMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IslemTableMapRepository extends JpaRepository<IslemTableMap, Long> {
    IslemTableMap findByIslemAdi(String islemAdi);
}

