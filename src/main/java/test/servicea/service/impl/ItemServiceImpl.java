package test.servicea.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import test.servicea.domain.Item;
import test.servicea.domain.dto.ItemDto;
import test.servicea.repository.ItemRepository;
import test.servicea.service.ItemService;

/**
 * Implementation of ItemService.
 */
@Service
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;

  /**
   * Constructs an ItemServiceImpl with the provided repository.
   *
   * @param itemRepository the repository used to access and persist items
   */
  public ItemServiceImpl(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  @Override
  public Item createItem(ItemDto itemDto) {
    if (itemDto == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item cannot be null");
    }
    Item item =
        new Item(itemDto.getName(), itemDto.getStock(), itemDto.getPrice(), itemDto.getDescription());
    itemRepository.save(item);
    return item;
  }

  @Override
  public List<Item> getAllItems(boolean multiCatalog) {
    // the multiCatalog parameter is ignored in this implementation
    List<Item> items = itemRepository.findAll();
    if (!items.isEmpty()) {
      return items;
    }
    return List.of();
  }

  @Override
  public Item getItemById(int id) {
    Optional<Item> item = itemRepository.findById(id);
    return item.orElse(null);
  }

  @Override
  public Item updateItemById(int id, ItemDto itemDto) {
    Optional<Item> item = itemRepository.findById(id);
    if (item.isPresent()) {
      Item itemToUpdate = item.get();
      itemToUpdate.setName(itemDto.getName());
      itemToUpdate.setStock(itemDto.getStock());
      itemToUpdate.setPrice(itemDto.getPrice());
      itemToUpdate.setDescription(itemDto.getDescription());
      itemRepository.save(itemToUpdate);
      return itemToUpdate;
    }
    return null;
  }

  @Override
  public Item getItemByIdAndName(int id, String name) {
    return itemRepository.findItemByIdAndName(id, name);
  }

  @Override
  public Item updateItemByIdAndName(int id, String name, ItemDto itemDto) {
    Item item = getItemByIdAndName(id, name);
    if (item != null) {
      item.setName(itemDto.getName());
      item.setStock(itemDto.getStock());
      item.setPrice(itemDto.getPrice());
      item.setDescription(itemDto.getDescription());
      itemRepository.save(item);
      return item;
    }
    return null;
  }
}
