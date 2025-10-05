package test.servicea.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.servicea.domain.Cat;

/**
 * Spring Data repository for Cat entities.
 */
@Repository
public interface CatRepository extends JpaRepository<Cat, Integer> {
}
