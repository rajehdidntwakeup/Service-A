package test.servicea.service;

import java.util.List;

import org.springframework.web.server.ResponseStatusException;
import test.servicea.domain.Item;
import test.servicea.domain.dto.ItemDto;

/**
 * Service interface for Picture entities.
 */
public interface ItemService {

  /**
   * Creates an Item entity.
   *
   * @param itemDto the Item to create
   * @return the created Item entity
   * @throws ResponseStatusException if the provided ItemDto is null
   */
  Item createItem(ItemDto itemDto);

  /**
   * Retrieves a list of all Item entities. The list may include items
   * from either a single catalog or multiple catalogs, based on the provided flag.
   *
   * @param multiCatalog a boolean flag indicating whether to include items
   *                      from multiple catalogs (true) or a single catalog (false)
   * @return a list of Item entities
   */
  List<Item> getAllItems(boolean multiCatalog);

  /**
   * Retrieves a Item entity by its unique ID.
   *
   * @param id the unique ID of the Item to retrieve
   * @return the Item entity with the specified ID, or null if no Item with that ID exists
   */
  Item getItemById(int id);

  /**
   * Updates an existing Item entity.
   *
   * @param id the unique ID of the Item to update
   * @param itemDto the Item to update with
   * @return the updated Item entity
   */
  Item updateItemById(int id, ItemDto itemDto);


}
