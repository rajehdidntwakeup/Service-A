package test.servicea.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.servicea.domain.Item;

/**
 * Spring Data repository for Item entities.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

  /**
   * Retrieves an Item entity by its unique identifier and name.
   *
   * @param id   the unique identifier of the item
   * @param name the name of the item
   * @return the Item entity that matches the provided id and name or null if no such item exists
   */
  Item findItemByIdAndName(int id, String name);
}
