package test.servicea.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import test.servicea.domain.Item;
import test.servicea.domain.dto.ExternalInventory;
import test.servicea.domain.dto.ItemDto;
import test.servicea.repository.ItemRepository;
import test.servicea.service.ItemService;
import test.servicea.service.converter.ConversionProperties;

/**
 * Implementation of ItemService.
 */
@Service
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;
  private final ConversionProperties properties;
  private final RestTemplate restTemplate;

  /**
   * Constructs an ItemServiceImpl with the provided repository.
   *
   * @param itemRepository the repository used to access and persist items
   */
  public ItemServiceImpl(ItemRepository itemRepository, ConversionProperties properties, RestTemplateBuilder restTemplateBuilder) {
    this.itemRepository = itemRepository;
    this.properties = properties;
    this.restTemplate = restTemplateBuilder.build();
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
    List<Item> allItems = new ArrayList<>();
    List<Item> items = itemRepository.findAll();
    if (!items.isEmpty()) {
      allItems.addAll(items);
    }
    if (multiCatalog) {
      allItems.addAll(callExternalServices());
    }
    return allItems;
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


  /**
   * Fetches items from all configured external inventory services.
   * Iterates through the external inventory configurations, makes HTTP GET requests
   * to retrieve items from each external service endpoint, and aggregates the results.
   * If an error occurs during a request, it is handled and the process continues with other services.
   *
   * @return a list of items retrieved from external inventory services. If no items are retrieved
   *         or if there are no configured external services, the list will be empty.
   */
  private List<Item> callExternalServices() {
    List<Item> externalItems = new ArrayList<>();
    for (Map.Entry<String, ExternalInventory> entry : properties.getExternalInventory().entrySet()) {
      ExternalInventory externalService = entry.getValue();
      String url = externalService.getUrl();

      try {
        Item[] response = restTemplate.getForObject(url, Item[].class);
        if (response != null) {
          externalItems.addAll(List.of(response));
        }
      } catch (Exception e) {
        // Optional: log and continue
        System.err.println("Failed to call " + externalService.getName() + " at " + url);
      }
    }
    return externalItems;
  }
}
