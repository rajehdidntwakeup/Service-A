package test.servicea.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.servicea.domain.Item;

/**
 * Spring Data repository for Item entities.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
}
