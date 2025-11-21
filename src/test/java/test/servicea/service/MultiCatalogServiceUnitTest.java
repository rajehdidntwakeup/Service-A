package test.servicea.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import test.servicea.domain.Item;
import test.servicea.domain.dto.ExternalInventory;
import test.servicea.repository.ItemRepository;
import test.servicea.service.converter.ConversionProperties;
import test.servicea.service.impl.ItemServiceImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MultiCatalogServiceUnitTest {

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private ConversionProperties properties;

  @Mock
  private RestTemplateBuilder restTemplateBuilder;

  @Mock
  private RestTemplate restTemplate;

  @Test
  void getAllItems_multiCatalog_true_aggregatesRepositoryAndExternal() {
    // Arrange repository items
    Item repoItem1 = new Item("RepoA", 5, 10.0, "rA");
    when(itemRepository.findAll()).thenReturn(List.of(repoItem1));

    // Arrange external inventories (two services)
    Map<String, ExternalInventory> externalMap = new LinkedHashMap<>();
    externalMap.put("svcB", new ExternalInventory("B", "http://b.example/api/items"));
    externalMap.put("svcC", new ExternalInventory("C", "http://c.example/api/items"));
    when(properties.getExternalInventory()).thenReturn(externalMap);

    // Arrange RestTemplate building and responses
    when(restTemplateBuilder.build()).thenReturn(restTemplate);
    Item[] bItems = new Item[] { new Item("B1", 1, 1.0, "b1"), new Item("B2", 2, 2.0, "b2") };
    Item[] cItems = new Item[] { new Item("C1", 3, 3.0, "c1") };
    when(restTemplate.getForObject(eq("http://b.example/api/items"), eq(Item[].class))).thenReturn(bItems);
    when(restTemplate.getForObject(eq("http://c.example/api/items"), eq(Item[].class))).thenReturn(cItems);

    // Build service after stubbing builder
    ItemServiceImpl service = new ItemServiceImpl(itemRepository, properties, restTemplateBuilder);

    // Act
    List<Item> all = service.getAllItems(true);

    // Assert
    assertNotNull(all);
    // 1 from repo + 2 from B + 1 from C = 4
    assertEquals(4, all.size());
    verify(itemRepository, times(1)).findAll();
    verify(restTemplate, times(1)).getForObject("http://b.example/api/items", Item[].class);
    verify(restTemplate, times(1)).getForObject("http://c.example/api/items", Item[].class);
  }

  @Test
  void getAllItems_multiCatalog_true_withEmptyExternalMap_returnsOnlyRepoItems() {
    // Repo has items
    Item r1 = new Item("R1", 1, 1.0, "r1");
    Item r2 = new Item("R2", 2, 2.0, "r2");
    when(itemRepository.findAll()).thenReturn(List.of(r1, r2));

    // External inventory map is empty
    when(properties.getExternalInventory()).thenReturn(new LinkedHashMap<>());

    // Do not stub builder; even if RestTemplate is null, no external calls will be attempted
    ItemServiceImpl service = new ItemServiceImpl(itemRepository, properties, restTemplateBuilder);

    List<Item> all = service.getAllItems(true);

    assertEquals(2, all.size());
    assertEquals("R1", all.getFirst().getName());
    // No external calls should be performed
    verify(restTemplate, never()).getForObject(any(String.class), eq(Item[].class));
  }

  @Test
  void getAllItems_multiCatalog_true_externalReturnsEmptyArray_resultsRemainUnchanged() {
    // Repo empty
    when(itemRepository.findAll()).thenReturn(List.of());

    // One external returning empty array
    Map<String, ExternalInventory> map = new LinkedHashMap<>();
    map.put("svcEmpty", new ExternalInventory("Empty", "http://empty.example/api/items"));
    when(properties.getExternalInventory()).thenReturn(map);

    when(restTemplateBuilder.build()).thenReturn(restTemplate);
    when(restTemplate.getForObject(eq("http://empty.example/api/items"), eq(Item[].class)))
        .thenReturn(new Item[]{});

    ItemServiceImpl service = new ItemServiceImpl(itemRepository, properties, restTemplateBuilder);

    List<Item> all = service.getAllItems(true);

    assertNotNull(all);
    assertTrue(all.isEmpty(), "No items should be aggregated when external returns empty array");
    verify(restTemplate, times(1)).getForObject("http://empty.example/api/items", Item[].class);
  }

  @Test
  void getAllItems_multiCatalog_true_preservesOrder_andAlllowsDuplicates() {
    // Repo items
    Item r1 = new Item("X", 1, 1.0, "r1");
    Item r2 = new Item("Y", 2, 2.0, "r2");
    when(itemRepository.findAll()).thenReturn(List.of(r1, r2));

    // External inventories in deterministic order: S1 then S2
    Map<String, ExternalInventory> map = new LinkedHashMap<>();
    map.put("S1", new ExternalInventory("S1", "http://s1/items"));
    map.put("S2", new ExternalInventory("S2", "http://s2/items"));
    when(properties.getExternalInventory()).thenReturn(map);

    when(restTemplateBuilder.build()).thenReturn(restTemplate);
    // Return items including a duplicate name "X" to ensure no de-duplication happens
    Item[] s1 = new Item[]{ new Item("S1-A", 3, 3.0, "a"), new Item("X", 9, 9.0, "dup") };
    Item[] s2 = new Item[]{ new Item("S2-A", 4, 4.0, "b") };
    when(restTemplate.getForObject(eq("http://s1/items"), eq(Item[].class))).thenReturn(s1);
    when(restTemplate.getForObject(eq("http://s2/items"), eq(Item[].class))).thenReturn(s2);

    ItemServiceImpl service = new ItemServiceImpl(itemRepository, properties, restTemplateBuilder);

    List<Item> all = service.getAllItems(true);

    // Expected order: repo [X, Y] then external in map order [S1-A, X, S2-A]
    assertEquals(List.of("X", "Y", "S1-A", "X", "S2-A"),
        all.stream().map(Item::getName).toList());
  }

  @Test
  void getAllItems_multiCatalog_true_nullUrlEntry_isCaught_andOtherServicesContinue() {
    when(itemRepository.findAll()).thenReturn(List.of());

    Map<String, ExternalInventory> map = new LinkedHashMap<>();
    map.put("NULL", new ExternalInventory("NullUrl", null));
    map.put("OK", new ExternalInventory("OkUrl", "http://ok.example/items"));
    when(properties.getExternalInventory()).thenReturn(map);

    when(restTemplateBuilder.build()).thenReturn(restTemplate);
    // Simulate an exception when URL is null, and valid items for the OK service
    when(restTemplate.getForObject(isNull(String.class), eq(Item[].class)))
        .thenThrow(new IllegalArgumentException("URL is null"));
    Item[] ok = new Item[]{ new Item("OK1", 1, 1.0, "d") };
    when(restTemplate.getForObject(eq("http://ok.example/items"), eq(Item[].class))).thenReturn(ok);

    ItemServiceImpl service = new ItemServiceImpl(itemRepository, properties, restTemplateBuilder);

    List<Item> all = service.getAllItems(true);

    assertEquals(1, all.size());
    assertEquals("OK1", all.getFirst().getName());
    verify(restTemplate, times(1)).getForObject(isNull(String.class), eq(Item[].class));
    verify(restTemplate, times(1)).getForObject("http://ok.example/items", Item[].class);
  }

  @Test
  void getAllItems_multiCatalog_true_handlesNullResponsesAndExceptions() {
    // Arrange repository items
    when(itemRepository.findAll()).thenReturn(new ArrayList<>());

    // Two external services: one returns null, one throws exception, one returns items
    Map<String, ExternalInventory> externalMap = new LinkedHashMap<>();
    externalMap.put("svcNull", new ExternalInventory("NullSvc", "http://null.example/api/items"));
    externalMap.put("svcErr", new ExternalInventory("ErrSvc", "http://err.example/api/items"));
    externalMap.put("svcOk", new ExternalInventory("OkSvc", "http://ok.example/api/items"));
    when(properties.getExternalInventory()).thenReturn(externalMap);

    when(restTemplateBuilder.build()).thenReturn(restTemplate);
    when(restTemplate.getForObject(eq("http://null.example/api/items"), eq(Item[].class))).thenReturn(null);
    when(restTemplate.getForObject(eq("http://err.example/api/items"), eq(Item[].class)))
        .thenThrow(new RuntimeException("boom"));
    Item[] okItems = new Item[] { new Item("OK1", 10, 9.9, "ok") };
    when(restTemplate.getForObject(eq("http://ok.example/api/items"), eq(Item[].class))).thenReturn(okItems);

    ItemServiceImpl service = new ItemServiceImpl(itemRepository, properties, restTemplateBuilder);

    // Act
    List<Item> all = service.getAllItems(true);

    // Assert
    assertNotNull(all);
    assertEquals(1, all.size(), "Only items from OK service should be added");
    assertEquals("OK1", all.getFirst().getName());
    verify(restTemplate, times(1)).getForObject("http://null.example/api/items", Item[].class);
    verify(restTemplate, times(1)).getForObject("http://err.example/api/items", Item[].class);
    verify(restTemplate, times(1)).getForObject("http://ok.example/api/items", Item[].class);
  }

  @Test
  void getAllItems_multiCatalog_false_doesNotInvokeExternal() {
    // Repo returns two items
    Item r1 = new Item("R1", 1, 1.0, "r1");
    Item r2 = new Item("R2", 2, 2.0, "r2");
    when(itemRepository.findAll()).thenReturn(List.of(r1, r2));

    // Even if external inventories are configured and RestTemplate exists,
    // with multiCatalog= false, we must not call external endpoints.
    // Do not stub external inventory or builder to avoid unnecessary stubbing warnings.

    ItemServiceImpl service = new ItemServiceImpl(itemRepository, properties, restTemplateBuilder);

    // Act
    List<Item> all = service.getAllItems(false);

    // Assert
    assertEquals(2, all.size());
    verify(itemRepository, times(1)).findAll();
    verify(restTemplate, never()).getForObject(any(String.class), eq(Item[].class));
  }

  @Test
  void getAllItems_multiCatalog_true_repositoryEmpty_usesOnlyExternal() {
    when(itemRepository.findAll()).thenReturn(List.of());

    Map<String, ExternalInventory> externalMap = new LinkedHashMap<>();
    externalMap.put("svcOne", new ExternalInventory("One", "http://one.example/api/items"));
    when(properties.getExternalInventory()).thenReturn(externalMap);

    when(restTemplateBuilder.build()).thenReturn(restTemplate);
    Item[] oneItems = new Item[] { new Item("E1", 7, 7.7, "e1"), new Item("E2", 8, 8.8, "e2") };
    when(restTemplate.getForObject(eq("http://one.example/api/items"), eq(Item[].class))).thenReturn(oneItems);

    ItemServiceImpl service = new ItemServiceImpl(itemRepository, properties, restTemplateBuilder);

    List<Item> all = service.getAllItems(true);

    assertEquals(2, all.size());
    assertEquals("E1", all.getFirst().getName());
    verify(itemRepository, times(1)).findAll();
    verify(restTemplate, times(1)).getForObject("http://one.example/api/items", Item[].class);
  }
  
  
}
