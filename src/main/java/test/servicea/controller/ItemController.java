package test.servicea.controller;


import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import test.servicea.domain.Item;
import test.servicea.domain.dto.ItemDto;
import test.servicea.service.ItemService;

/**
 * Controller for managing Item-related API endpoints.
 * Handles HTTP requests and responses for operations on Item entities.
 */
@RestController
@RequestMapping("/api/inventory")
public class ItemController {

  private final ItemService itemService;

  /**
   * Constructs an ItemController with the provided service.
   *
   * @param itemService the service used to access and persist items
   */
  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  /**
   * Creates a new item resource based on the provided information.
   *
   * @param itemDto the data transfer object containing details to create a new item
   * @return a ResponseEntity containing the created Item and an HTTP status of 201 (Created)
   */
  @PostMapping
  public ResponseEntity<Item> createItem(@Valid @RequestBody ItemDto itemDto) {
    Item item = itemService.createItem(itemDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(item);
  }

  /**
   * Retrieves a list of all items, optionally across multiple catalogs, based on the provided parameter.
   *
   * @param multiCatalog a boolean flag indicating whether to retrieve items
   *                     from multiple catalogs (true) or a single catalog (false);
   *                     defaults to false if not specified
   * @return a ResponseEntity containing a list of Item objects along with an HTTP status of 200 (OK)
   */
  @GetMapping
  public ResponseEntity<List<Item>> getAll(
      @RequestParam(name = "multi-catalog", required = false, defaultValue = "false") boolean multiCatalog
  ) {
    List<Item> items = itemService.getAllItems(multiCatalog);
    return ResponseEntity.ok(items);
  }

  /**
   * Retrieves a item resource by its unique identifier.
   *
   * @param id the unique identifier of the item to retrieve
   * @return a ResponseEntity containing the item if found with an HTTP status of 200 (OK),
   *         or an HTTP status of 404 (Not Found) if the item does not exist
   */
  @GetMapping("/{id}")
  public ResponseEntity<Item> getItemById(@PathVariable int id) {
    Item item = itemService.getItemById(id);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  /**
   * Updates an existing item resource identified by its unique identifier
   * using the data provided in the ItemDto object.
   *
   * @param id the unique identifier of the item to update
   * @param itemDto the data transfer object containing updated details for the item
   * @return a ResponseEntity containing the updated item with an HTTP status of 200 (OK)
   *         if the update is successful, or an HTTP status of 404 (Not Found)
   *         if the item with the specified ID does not exist
   */
  @PutMapping("/{id}")
  public ResponseEntity<Item> updateItemById(@PathVariable int id, @Valid @RequestBody ItemDto itemDto) {
    Item item = itemService.updateItemById(id, itemDto);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  /**
   * Retrieves an item resource based on its unique identifier and name.
   *
   * @param id the unique identifier of the item to retrieve
   * @param name the name of the item to retrieve
   * @return a ResponseEntity containing the item if found with an HTTP status of 200 (OK),
   *         or an HTTP status of 404 (Not Found) if the item does not exist
   */
  @GetMapping("/{id}/itemname/{name}")
  public ResponseEntity<Item> getItemByIdAndName(@PathVariable int id, @PathVariable String name) {
    Item item = itemService.getItemByIdAndName(id, name);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}/itemname/{name}")
  public ResponseEntity<Item> updateItemByIdAndName(@PathVariable int id, @PathVariable String name, @Valid @RequestBody ItemDto itemDto) {
    Item item = itemService.updateItemByIdAndName(id, name, itemDto);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
