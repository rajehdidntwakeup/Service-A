package test.servicea.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger LOG = LoggerFactory.getLogger(ItemServiceImpl.class);
  private static final String SERVICE_NAME = "Inventory-A";

  private final ItemRepository itemRepository;
  private final ConversionProperties properties;
  private final RestTemplate restTemplate;



  /**
   * Constructs an instance of ItemServiceImpl with the specified dependencies.
   *
   * @param itemRepository     the repository used for item-related database operations
   * @param properties         the properties used for external service configurations
   * @param templateBuilder the builder used to create RestTemplate instances for HTTP requests
   */
  public ItemServiceImpl(ItemRepository itemRepository, ConversionProperties properties,
                         RestTemplateBuilder templateBuilder) {
    this.itemRepository = itemRepository;
    this.properties = properties;
    this.restTemplate = templateBuilder.build();
  }

  @Override
  public Item createItem(ItemDto itemDto) {
    if (itemDto == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item cannot be null");
    }
    String name = validateItemName(itemDto.getName());
    Item item =
        new Item(name, itemDto.getStock(), itemDto.getPrice(), itemDto.getDescription());
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
      return getItem(itemDto, itemToUpdate);
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
      return getItem(itemDto, item);
    }
    return null;
  }


  /**
   * Updates the properties of an existing item based on the provided ItemDto
   * and persists the updated item in the repository.
   *
   * @param itemDto the data transfer object containing the new item properties
   * @param item    the existing item to be updated
   * @return the updated item after persisting changes
   */
  private Item getItem(ItemDto itemDto, Item item) {
    String validatedName = validateItemName(itemDto.getName());
    item.setName(validatedName);
    item.setStock(itemDto.getStock());
    item.setPrice(itemDto.getPrice());
    item.setDescription(itemDto.getDescription());
    itemRepository.save(item);
    return item;
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
        if (LOG.isWarnEnabled()) {
          LOG.warn("Failed to call {} at {}", externalService.getName(), url, e);
        }
      }
    }
    return externalItems;
  }

  /**
   * Validates the given item name by ensuring it contains the service name.
   * If the service name is not present, it prefixes the name with the service name.
   *
   * @param name the name of the item to validate
   * @return the validated item name, prefixed with the service name if necessary
   */
  private String validateItemName(String name) {
    if (!name.contains(SERVICE_NAME)) {
      return SERVICE_NAME + ": " + name;
    }
    return name;
  }
}
